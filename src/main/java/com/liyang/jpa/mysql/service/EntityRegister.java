package com.liyang.jpa.mysql.service;

import java.lang.reflect.Field;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.ResolvableType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liyang.jpa.mysql.config.JpaSmartQuerySupport;
import com.liyang.jpa.mysql.db.structure.ColumnFormat;
import com.liyang.jpa.mysql.db.structure.ColumnJoinType;
import com.liyang.jpa.mysql.db.structure.ColumnStucture;
import com.liyang.jpa.mysql.db.structure.EntityStructure;
import com.liyang.jpa.mysql.db.structure.Stopword;
import com.liyang.jpa.mysql.exception.StructureException;

@Service
public class EntityRegister implements ApplicationContextAware {

	protected final static Logger logger = LoggerFactory.getLogger(EntityRegister.class);
	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@PostConstruct
	public void registerAllEntity() {
		logger.info("JpaSmartQuery start check Entity");
		_checkDomain();
	}

	private void _checkDomain() {
		Map<String, JpaRepository> beans = applicationContext.getBeansOfType(JpaRepository.class);
		logger.info(beans.toString());
		for (JpaRepository jpaRepository : beans.values()) {
			ResolvableType resolvableType = ResolvableType.forClass(jpaRepository.getClass());
			Class<?> entityClass = resolvableType.as(JpaRepository.class).getGeneric(0).resolve();
			_entityStructureCheckAndRegister(entityClass, jpaRepository);
		}
	}

	// 实体类的注解
	private void _entityStructureCheckAndRegister(Class<?> entityClass, JpaRepository jpaRepository) {
		Table tableAnnotation = entityClass.getDeclaredAnnotation(Table.class);
		if (tableAnnotation == null || "".equals(tableAnnotation.name())) {
			throw new StructureException("实体 " + entityClass.getSimpleName() + " 缺少带name属性的table注解");
		}
		if (Stopword.contains(tableAnnotation.name().toUpperCase())) {
			throw new StructureException("实体类不允许用关键词 " + tableAnnotation.name() + " 做表明");
		}

		// 加入结构
		EntityStructure entityStructure = new EntityStructure();
		entityStructure.setEntityClass(entityClass);
		entityStructure.setJpaRepository(jpaRepository);
		entityStructure.setTableName(tableAnnotation.name());
		entityStructure.setName(
				entityClass.getSimpleName().substring(0, 1).toLowerCase() + entityClass.getSimpleName().substring(1));
		Field[] declaredFields = entityClass.getDeclaredFields();
		for (Field field : declaredFields) {
			_columnCheck(field, entityStructure);
		}

		JpaSmartQuerySupport.getNametostructure().put(entityStructure.getName(), entityStructure);
		JpaSmartQuerySupport.getClasstostructure().put(entityClass, entityStructure);

	}

	// 属�?�检查，并加入结构中
	private void _columnCheck(Field field, EntityStructure entityStructure) {

		if (field.getDeclaredAnnotation(JsonIgnore.class) != null) {
			return;
		} else if (field.getName().equals("serialVersionUID")) {
			return;
		}

		Column columnAnnotation = field.getDeclaredAnnotation(Column.class);
		if (columnAnnotation != null) {
			if ("".equals(columnAnnotation.name())) {
				throw new StructureException("实体" + field.getDeclaringClass().getSimpleName() + "的属性" + field.getName()
						+ "的@Column注解没有name");
			}
			ColumnStucture column = new ColumnStucture(ColumnFormat.SIMPLE, null, null, field.getType(),
					entityStructure.getTableName(), null, null, columnAnnotation.name());
			Class<?> type = field.getType();
			if (type.isPrimitive()) {
				throw new StructureException(
						"实体" + field.getDeclaringClass().getSimpleName() + "的属性" + field.getName() + " 必须为包装类 ");
			}
			entityStructure.getSimpleFields().put(field.getName(), column);
			return;
		}
		// manytoone
		ManyToOne manyToOneAnnotation = field.getDeclaredAnnotation(ManyToOne.class);
		if (manyToOneAnnotation != null) {
			JoinColumn joinColumnAnnotation = field.getDeclaredAnnotation(JoinColumn.class);
			if (joinColumnAnnotation == null || "".equals(joinColumnAnnotation.name())) {
				throw new StructureException("实体" + field.getDeclaringClass().getSimpleName() + "的属性" + field.getName()
						+ "没有@JoinColumn注解，或者注解没有设置name");
			}
			ColumnStucture column = new ColumnStucture(ColumnFormat.OBJECT, ColumnJoinType.MANY_TO_ONE, null,
					field.getType(), null, joinColumnAnnotation.name(), null, null);
			entityStructure.getObjectFields().put(field.getName(), column);
			return;
		}

		// manytomany
		ManyToMany manyToManyAnnotation = field.getDeclaredAnnotation(ManyToMany.class);
		if (manyToManyAnnotation != null) {
			try {
				ResolvableType resolvableType = ResolvableType.forField(field);
				Class<?> columnType = resolvableType.getGeneric(0).resolve();

				if ("".equals(manyToManyAnnotation.mappedBy())) {
					JoinTable joinTableAnnotation = field.getDeclaredAnnotation(JoinTable.class);
					if (joinTableAnnotation == null || "".equals(joinTableAnnotation.name())
							|| joinTableAnnotation.joinColumns().length == 0
							|| joinTableAnnotation.inverseJoinColumns().length == 0) {
						throw new StructureException("实体" + field.getDeclaringClass().getSimpleName() + "的属性"
								+ field.getName() + "没有@JoinTable注解，或者注解设置不全");
					}
					ColumnStucture column = new ColumnStucture(ColumnFormat.OBJECT, ColumnJoinType.MANY_TO_MANY, null,
							columnType, joinTableAnnotation.name(), joinTableAnnotation.joinColumns()[0].name(),
							joinTableAnnotation.inverseJoinColumns()[0].name(), null);
					entityStructure.getObjectFields().put(field.getName(), column);
				} else {

					Field otherField;
					otherField = columnType.getDeclaredField(manyToManyAnnotation.mappedBy());
					String joinTable = otherField.getDeclaredAnnotation(JoinTable.class).name();
					String joinColumn = otherField.getDeclaredAnnotation(JoinTable.class).inverseJoinColumns()[0]
							.name();
					String inverseJoinColumn = otherField.getDeclaredAnnotation(JoinTable.class).joinColumns()[0]
							.name();
					ColumnStucture column = new ColumnStucture(ColumnFormat.OBJECT, ColumnJoinType.MANY_TO_MANY,
							manyToManyAnnotation.mappedBy(), columnType, joinTable, joinColumn, inverseJoinColumn,
							null);
					entityStructure.getObjectFields().put(field.getName(), column);

				}
			} catch (NoSuchFieldException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}

		// onetoone
		OneToOne oneToOneAnnotation = field.getDeclaredAnnotation(OneToOne.class);
		if (oneToOneAnnotation != null) {
			if ("".equals(oneToOneAnnotation.mappedBy())) {
				JoinColumn joinColumnAnnotation = field.getDeclaredAnnotation(JoinColumn.class);
				if (joinColumnAnnotation == null || "".equals(joinColumnAnnotation.name())) {
					throw new StructureException("实体" + field.getDeclaringClass().getSimpleName() + "的属性"
							+ field.getName() + "没有@JoinColumn注解，或者没有设置name");
				}

				ColumnStucture column = new ColumnStucture(ColumnFormat.OBJECT, ColumnJoinType.ONE_TO_ONE, null,
						field.getType(), entityStructure.getTableName(), joinColumnAnnotation.name(), null, null);
				entityStructure.getObjectFields().put(field.getName(), column);
			} else {
				Field otherField;
				try {
					otherField = field.getType().getDeclaredField(oneToOneAnnotation.mappedBy());
					String joinTable = field.getType().getDeclaredAnnotation(Table.class).name();
					String joinColumn = otherField.getDeclaredAnnotation(JoinColumn.class).name();

					ColumnStucture column = new ColumnStucture(ColumnFormat.OBJECT, ColumnJoinType.ONE_TO_ONE,
							oneToOneAnnotation.mappedBy(), field.getType(), joinTable, joinColumn, null, null);
					entityStructure.getObjectFields().put(field.getName(), column);
				} catch (NoSuchFieldException | SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return;
		}

		// onetomany
		OneToMany oneToManyAnnotation = field.getDeclaredAnnotation(OneToMany.class);
		if (oneToManyAnnotation != null) {
			if ("".equals(oneToManyAnnotation.mappedBy())) {
				throw new StructureException(
						"实体" + field.getDeclaringClass().getSimpleName() + "的属性" + field.getName() + "没有设置mappedBy");
			}
			try {
				Field otherField;

				ResolvableType resolvableType = ResolvableType.forField(field);
				Class<?> resolve = resolvableType.getGeneric(0).resolve();
				otherField = resolve.getDeclaredField(oneToManyAnnotation.mappedBy());

				String joinTable = resolve.getDeclaredAnnotation(Table.class).name();
				String joinColumn = otherField.getDeclaredAnnotation(JoinColumn.class).name();

				ColumnStucture column = new ColumnStucture(ColumnFormat.OBJECT, ColumnJoinType.ONE_TO_MANY,
						oneToManyAnnotation.mappedBy(), resolve, joinTable, joinColumn, null, null);
				entityStructure.getObjectFields().put(field.getName(), column);
			} catch (NoSuchFieldException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return;
		}

		// transient
		Transient transientAnnotation = field.getDeclaredAnnotation(Transient.class);
		if (transientAnnotation != null) {
			return;
		} else {
			throw new StructureException(
					"实体" + field.getDeclaringClass().getSimpleName() + "的属性" + field.getName() + "没有设置正确");
		}

	}
}
