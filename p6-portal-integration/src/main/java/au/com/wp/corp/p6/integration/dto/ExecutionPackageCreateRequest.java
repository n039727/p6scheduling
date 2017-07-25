package au.com.wp.corp.p6.integration.dto;

public class ExecutionPackageCreateRequest {
	
	private String filter;
	private String orderBy;
	private Integer foreignObjectId;
	private String text;
	private String udfTypeDataType;
	private Integer udfTypeObjectId;
	private String udfTypeSubjectArea;
	private String udfTypeTitle;
	public Integer getForeignObjectId() {
		return foreignObjectId;
	}
	public void setForeignObjectId(Integer foreignObjectId) {
		this.foreignObjectId = foreignObjectId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getUdfTypeDataType() {
		return udfTypeDataType;
	}
	public void setUdfTypeDataType(String udfTypeDataType) {
		this.udfTypeDataType = udfTypeDataType;
	}
	public Integer getUdfTypeObjectId() {
		return udfTypeObjectId;
	}
	public void setUdfTypeObjectId(Integer udfTypeObjectId) {
		this.udfTypeObjectId = udfTypeObjectId;
	}
	public String getUdfTypeSubjectArea() {
		return udfTypeSubjectArea;
	}
	public void setUdfTypeSubjectArea(String udfTypeSubjectArea) {
		this.udfTypeSubjectArea = udfTypeSubjectArea;
	}
	public String getUdfTypeTitle() {
		return udfTypeTitle;
	}
	public void setUdfTypeTitle(String udfTypeTitle) {
		this.udfTypeTitle = udfTypeTitle;
	}
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	 
}
