package com.gentics.mesh.core.data.model.tinkerpop;

import org.jglue.totorom.FramedEdge;

public class Linked extends FramedEdge {

	public MeshNode getStartNode() {
		return inV().next(MeshNode.class);
	}

	public MeshNode getEndNode() {
		return outV().next(MeshNode.class);
	}

}
