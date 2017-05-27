/**
 * 
 */
package au.com.wp.corp.p6.wsclient.constant;

/**
 * 
 * Holds all the constant required for P6 Web service call
 * 
 * @author N039126
 *
 */
public enum P6WSConstantsEnum {
	/**
	 * Ellipse task user status - object Id of P6 UDF Type
	 */
	ELLIPSE_TASK_USER_STATUS_TYP_ID (5934),
	/**
	 * Ellipse Equipment no - object Id of P6 UDF Type
	 */
	ELLIPSE_EQUIPMENT_NO_TYP_ID(5908),
	/**
	 * Ellipse Pick Id - object Id of P6 UDF Type
	 */
	ELLIPSE_PICK_ID_TYP_ID(5935),
	/**
	 * Ellipse EGI - object Id of P6 UDF Type
	 */
	ELLIPSE_EGI_TYP_ID(5911),
	/**
	 * Ellipse Equip code - object Id of P6 UDF Type
	 */
	ELLIPSE_EQUIP_CODE_TYP_ID(5912),
	/**
	 * Ellipse Required By Date - object Id of P6 UDF Type
	 */
	ELLIPSE_REQ_BY_DATE_TYP_ID(5927),
	/**
	 * Ellipse Standard Job - object Id of P6 UDF Type
	 */
	ELLIPSE_STD_JOB_TYP_ID (5932),
	/**
	 * Ellipse Feeder - object Id of P6 UDF Type
	 */
	ELLIPSE_FEEEDER_TYP_ID(5930),
	/**
	 * Ellipse Upstream Switch - object Id of P6 UDF Type
	 */
	ELLIPSE_UPSTREAM_SWITCH_TYP_ID (5931);
	
	private int objectId;
	
	private P6WSConstantsEnum (int objectId){
		this.objectId = objectId;
	}
	
	
	public int value() {
        return this.objectId;
    }
	

}
