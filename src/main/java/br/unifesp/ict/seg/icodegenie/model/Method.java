package br.unifesp.ict.seg.icodegenie.model;

import br.unifesp.ict.seg.geniesearchapi.domain.GenieMethod;
import edu.uci.ics.sourcerer.services.search.adapter.SingleResult;

public class Method {

	private SingleResult singleResult;
	private GenieMethod genieMethod;

	public Method(SingleResult singleResult) {
		this.singleResult = singleResult;
	}

	public int getRank() {
		return singleResult.getRank();
	}

	public float getScore() {
		return singleResult.getScore();
	}

	public long getEntityId() {
		return singleResult.getEntityID();
	}

	public String getFqn() {
		return singleResult.getFqn();
	}

	public int getParamCount() {
		return singleResult.getParamCount();
	}

	public String getParams() {
		return singleResult.getParams();
	}

	public String getReturnFqn() {
		return singleResult.getReturnFqn();
	}

	// Accessors
	public GenieMethod getGenieMethod() {
		return genieMethod;
	}

	public void setGenieMethod(GenieMethod genieMethod) {
		this.genieMethod = genieMethod;
	}
}
