/**
 * 
 */
package au.com.wp.corp.p6.wsclient.constant;

/**
 * 
 * Holds all the constant required for P6 Web service call
 * 
 * List of UDFType Filed available in P6
 * 
 * <UDFType> <DataType>Text</DataType> <ObjectId>5908</ObjectId>
 * <SubjectArea>Activity</SubjectArea> <Title>Ellipse Equipment No</Title>
 * </UDFType> <UDFType> <DataType>Text</DataType> <ObjectId>5911</ObjectId>
 * <SubjectArea>Activity</SubjectArea> <Title>Ellipse EGI</Title> </UDFType>
 * <UDFType> <DataType>Text</DataType> <ObjectId>5912</ObjectId>
 * <SubjectArea>Activity</SubjectArea> <Title>Ellipse Equip Code</Title>
 * </UDFType> <UDFType> <DataType>Start Date</DataType>
 * <ObjectId>5917</ObjectId> <SubjectArea>Activity</SubjectArea> <Title>Ellipse
 * Start Date</Title> </UDFType> <UDFType> <DataType>Text</DataType>
 * <ObjectId>5920</ObjectId> <SubjectArea>Activity</SubjectArea>
 * <Title>Execution Grouping</Title> </UDFType> <UDFType>
 * <DataType>Text</DataType> <ObjectId>5927</ObjectId>
 * <SubjectArea>Activity</SubjectArea> <Title>Ellipse Req By Date</Title>
 * </UDFType> <UDFType> <DataType>Double</DataType> <ObjectId>5929</ObjectId>
 * <SubjectArea>Activity</SubjectArea> <Title>Ellipse Est Lab Hours</Title>
 * </UDFType> <UDFType> <DataType>Text</DataType> <ObjectId>5930</ObjectId>
 * <SubjectArea>Activity</SubjectArea> <Title>Ellipse Feeder</Title> </UDFType>
 * <UDFType> <DataType>Text</DataType> <ObjectId>5931</ObjectId>
 * <SubjectArea>Activity</SubjectArea> <Title>Ellipse UpStream Switch</Title>
 * </UDFType> <UDFType> <DataType>Text</DataType> <ObjectId>5932</ObjectId>
 * <SubjectArea>Activity</SubjectArea> <Title>Ellipse STD Job</Title> </UDFType>
 * <UDFType> <DataType>Text</DataType> <ObjectId>5934</ObjectId>
 * <SubjectArea>Activity</SubjectArea> <Title>Ellipse Task User Status</Title>
 * </UDFType> <UDFType> <DataType>Text</DataType> <ObjectId>5935</ObjectId>
 * <SubjectArea>Activity</SubjectArea> <Title>Ellipse Pick Id</Title> </UDFType>
 * <UDFType> <DataType>Text</DataType> <ObjectId>5936</ObjectId>
 * <SubjectArea>Activity</SubjectArea> <Title>Ellipse Address</Title> </UDFType>
 * <UDFType> <DataType>Text</DataType> <ObjectId>5937</ObjectId>
 * <SubjectArea>Activity</SubjectArea> <Title>Ellipse Task Description</Title>
 * </UDFType> <UDFType> <DataType>Text</DataType> <ObjectId>5938</ObjectId>
 * <SubjectArea>Activity</SubjectArea> <Title>Ellipse JD Code</Title> </UDFType>
 * 
 * @author N039126
 *
 */
public interface P6EllipseWSConstants {
	

	/**
	 * Subject area defined in P6
	 */
	public static final String SUBJECT_AREA = "Activity";

	/**
	 * 1.Ellipse task user status - object Id of P6 UDF Type
	 */
	public static final String ELLIPSE_TASK_USER_STATUS_TITLE = "ELLIPSE_TASK_USER_STATUS_TITLE";
	/**
	 * 2.Ellipse Equipment no - object Id of P6 UDF Type
	 */
	public static final String ELLIPSE_EQUIPMENT_NO_TITLE = "ELLIPSE_EQUIPMENT_NO_TITLE";
	/**
	 * 3.Ellipse Pick Id - object Id of P6 UDF Type
	 */
	public static final String ELLIPSE_PICK_ID_TITLE = "ELLIPSE_PICK_ID_TITLE";
	/**
	 * 4.Ellipse EGI - object Id of P6 UDF Type
	 */
	public static final String ELLIPSE_EGI_TITLE = "ELLIPSE_EGI_TITLE";
	/**
	 * 5.Ellipse Equip code - object Id of P6 UDF Type
	 */
	public static final String ELLIPSE_EQUIP_CODE_TITLE = "ELLIPSE_EQUIP_CODE_TITLE";
	/**
	 * 6.Ellipse Required By Date - object Id of P6 UDF Type
	 */
	public static final String ELLIPSE_REQ_BY_DATE_TITLE = "ELLIPSE_REQ_BY_DATE_TITLE";
	/**
	 * 7.Ellipse Standard Job - object Id of P6 UDF Type
	 */
	public static final String ELLIPSE_STD_JOB_TITLE = "ELLIPSE_STD_JOB_TITLE";
	/**
	 * 8.Ellipse Feeder - object Id of P6 UDF Type
	 */
	public static final String ELLIPSE_FEEEDER_TITLE = "ELLIPSE_FEEEDER_TITLE";
	/**
	 * 9.Ellipse Upstream Switch - object Id of P6 UDF Type
	 */
	public static final String ELLIPSE_UPSTREAM_SWITCH_TITLE = "ELLIPSE_UPSTREAM_SWITCH_TITLE";

	/**
	 * 10.Ellipse JD Code - object Id of P6 UDF Type
	 */
	public static final String ELLIPSE_JD_CODE_TITLE = "ELLIPSE_JD_CODE_TITLE";

	/**
	 * 11.Ellipse TASK DESC - object Id of P6 UDF Type
	 */
	public static final String ELLIPSE_TASK_DESC_TITLE = "ELLIPSE_TASK_DESC_TITLE";

	/**
	 * 12.Ellipse Address - object Id of P6 UDF Type
	 */
	public static final String ELLIPSE_ADDRESS_TITLE = "ELLIPSE_ADDRESS_TITLE";
	/**
	 * 13. Ellipse Estimated Labor hours - object Id of P6 UDF Type
	 */
	public static final String ELLIPSE_EST_LAB_HOURS_TITLE = "ELLIPSE_EST_LAB_HOURS_TITLE";

	/**
	 * 14. Ellipse Execution package - object Id of P6 UDF Type
	 */
	public static final String ELLIPSE_EXECUTION_PCKG_TITLE = "ELLIPSE_EXECUTION_PCKG_TITLE";
	
	
	public static final String P6_ACTIVITY_SERVICE_WSDL ="P6_ACTIVITY_SERVICE_WSDL";

	public static final String P6_AUTH_SERVICE_WSDL="P6_AUTH_SERVICE_WSDL";

	public static final String P6_USER_PRINCIPAL="P6_USER_PRINCIPAL";

	public static final String P6_USER_CREDENTIAL="P6_USER_CREDENTIAL";

	public static final String P6_DB_INSTANCE="P6_DB_INSTANCE";

	public static final String P6_RESOURCE_SERVICE_WSDL="P6_RESOURCE_SERVICE_WSDL";

	public static final String P6_UDF_SERVICE_WSDL="P6_UDF_SERVICE_WSDL";

	public static final String P6_UDF_TYPE_SERVICE_WSDL="P6_UDF_TYPE_SERVICE_WSDL";
	
	public static final String P6_RESOURCE_ASSIGNMENT_SERVICE_WSDL="P6_RESOURCE_ASSIGNMENT_SERVICE_WSDL";
	
	
	public static final String  FOREIGN_OBJECT_ID = "ForeignObjectId";
	
	public static final String  ACTIVITY_OBJECT_ID = "ActivityObjectId";
	
	public static final String NO_ACTVTY_TO_BE_PRCSSD_ATATIME_IN_P6 = "NO_ACTVTY_TO_BE_PRCSSD_ATATIME_IN_P6";

	public static final String  NO_ACTVTY_TO_BE_PRCSSD_ATATIME_IN_ELLIPSE = "NO_ACTVTY_TO_BE_PRCSSD_ATATIME_IN_ELLIPSE";
	public static final String Y="Y";
	
	public static final String RR= "RR";

}
