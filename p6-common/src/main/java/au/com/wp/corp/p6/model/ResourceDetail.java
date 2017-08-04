package au.com.wp.corp.p6.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RESOURCE_DETAILS database table.
 * 
 */
@Entity
@Table(name="RESOURCE_DETAILS")
@NamedQuery(name="ResourceDetail.findAll", query="SELECT r FROM ResourceDetail r")
public class ResourceDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="RSRC_ID")
	private long rsrcId;

	@Column(name="DEPOT_NAM")
	private String depotNam;

	@Column(name="GRP_NAM")
	private String grpNam;

	@Column(name="RSRC_NAM")
	private String rsrcNam;

	public ResourceDetail() {
	}

	public long getRsrcId() {
		return this.rsrcId;
	}

	public void setRsrcId(long rsrcId) {
		this.rsrcId = rsrcId;
	}

	public String getDepotNam() {
		return this.depotNam;
	}

	public void setDepotNam(String depotNam) {
		this.depotNam = depotNam;
	}

	public String getGrpNam() {
		return this.grpNam;
	}

	public void setGrpNam(String grpNam) {
		this.grpNam = grpNam;
	}

	public String getRsrcNam() {
		return this.rsrcNam;
	}

	public void setRsrcNam(String rsrcNam) {
		this.rsrcNam = rsrcNam;
	}

}