package com.gentics.mesh.core.data.fieldhandler.schema;

import static com.gentics.mesh.assertj.MeshAssertions.assertThat;
import static com.gentics.mesh.core.rest.schema.change.impl.SchemaChangeModel.CONTAINER_FIELD_KEY;
import static com.gentics.mesh.core.rest.schema.change.impl.SchemaChangeModel.CONTAINER_FLAG_KEY;
import static com.gentics.mesh.core.rest.schema.change.impl.SchemaChangeModel.DESCRIPTION_KEY;
import static com.gentics.mesh.core.rest.schema.change.impl.SchemaChangeModel.FIELD_ORDER_KEY;
import static com.gentics.mesh.core.rest.schema.change.impl.SchemaChangeModel.NAME_KEY;
import static com.gentics.mesh.core.rest.schema.change.impl.SchemaChangeOperation.UPDATESCHEMA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.gentics.mesh.core.data.schema.handler.SchemaComparator;
import com.gentics.mesh.core.rest.schema.Schema;
import com.gentics.mesh.core.rest.schema.change.impl.SchemaChangeModel;
import com.gentics.mesh.core.rest.schema.change.impl.SchemaChangeOperation;
import com.gentics.mesh.test.AbstractDBTest;
import com.gentics.mesh.util.FieldUtil;

public class SchemaComparatorSchemaTest extends AbstractDBTest {

	@Autowired
	private SchemaComparator comparator;

	@Test
	public void testEmptySchema() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).isEmpty();
	}

	@Test
	public void testSchemaFieldReorder() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		schemaA.addField(FieldUtil.createHtmlFieldSchema("first"));
		schemaA.addField(FieldUtil.createHtmlFieldSchema("second"));

		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaB.addField(FieldUtil.createHtmlFieldSchema("second"));
		schemaB.addField(FieldUtil.createHtmlFieldSchema("first"));
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).hasSize(1);
		assertThat(changes.get(0)).is(UPDATESCHEMA).hasProperty(FIELD_ORDER_KEY,
				new String[] { "displayFieldName", "segmentFieldName", "second", "first" });
	}

	@Test
	public void testSchemaFieldNoReorder() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		schemaA.addField(FieldUtil.createHtmlFieldSchema("first"));
		schemaA.addField(FieldUtil.createHtmlFieldSchema("second"));

		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaB.addField(FieldUtil.createHtmlFieldSchema("first"));
		schemaB.addField(FieldUtil.createHtmlFieldSchema("second"));
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).isEmpty();
	}

	@Test
	public void testSegmentFieldSame() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).isEmpty();
	}

	@Test
	public void testSegmentFieldUpdated() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaA.setSegmentField("segmentFieldName");
		schemaB.setSegmentField("displayFieldName");
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertEquals(SchemaChangeOperation.UPDATESCHEMA, changes.get(0).getOperation());
	}

	@Test
	public void testDisplayFieldUpdated() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaA.setDisplayField("displayFieldName");
		schemaB.setDisplayField("segmentFieldName");
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertEquals(SchemaChangeOperation.UPDATESCHEMA, changes.get(0).getOperation());
	}

	@Test
	public void testDisplayFieldSame() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).isEmpty();
	}

	@Test
	public void testContainerFlagUpdated() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaA.setContainer(true);
		schemaB.setContainer(false);
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).hasSize(1);
		assertThat(changes.get(0)).is(UPDATESCHEMA).hasProperty(CONTAINER_FLAG_KEY, false);
	}

	@Test
	public void testContainerFlagSame() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaA.setContainer(true);
		schemaB.setContainer(true);
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).isEmpty();

		schemaA.setContainer(false);
		schemaB.setContainer(false);
		changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).isEmpty();
	}

	@Test
	public void testSameDescription() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaA.setDescription("test123");
		schemaB.setDescription("test123");
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).isEmpty();
	}

	@Test
	public void testDescriptionUpdated() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaA.setDescription("test123");
		schemaB.setDescription("test123-changed");
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).hasSize(1);
		assertThat(changes.get(0)).is(UPDATESCHEMA).hasProperty(DESCRIPTION_KEY, "test123-changed");
	}

	@Test
	public void testDescriptionUpdatedToNull() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaA.setDescription("test123");
		schemaB.setDescription(null);
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).hasSize(1);
		assertThat(changes.get(0)).is(UPDATESCHEMA).hasProperty(DESCRIPTION_KEY, null);
	}

	@Test
	public void testSameName() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaA.setName("test123");
		schemaB.setName("test123");
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).isEmpty();
	}

	@Test
	public void testNameUpdated() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaA.setName("test123");
		schemaB.setName("test123-changed");
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).hasSize(1);
		assertThat(changes.get(0)).is(UPDATESCHEMA).hasProperty(NAME_KEY, "test123-changed");
	}

}
