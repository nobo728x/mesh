package com.gentics.mesh.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.gentics.mesh.Mesh;
import com.gentics.mesh.cli.MeshNameProvider;
import com.gentics.mesh.core.rest.MeshServerInfoModel;
import com.gentics.mesh.test.AbstractRestVerticleTest;
import com.orientechnologies.orient.core.OConstants;

public class RestInfoVerticleTest extends AbstractRestVerticleTest {

	@Test
	public void testGetInfo() {
		MeshServerInfoModel info = call(() -> getClient().getApiInfo());
		assertEquals(Mesh.getPlainVersion(), info.getMeshVersion());
		assertEquals("orientdb", info.getDatabaseVendor());
		assertEquals("dummy", info.getSearchVendor());
		assertEquals(new io.vertx.core.Starter().getVersion(), info.getVertxVersion());
		assertEquals(MeshNameProvider.getInstance().getName(), info.getMeshNodeId());
		assertEquals("The database version did not match.", OConstants.getVersion(), info.getDatabaseVersion());
		assertEquals("1.0", info.getSearchVersion());
	}

}
