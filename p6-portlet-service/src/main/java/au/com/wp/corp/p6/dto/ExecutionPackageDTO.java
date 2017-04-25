package au.com.wp.corp.p6.dto;

import java.util.List;

public class ExecutionPackageDTO {
	private String exctnPckgNam;
	private String leadCrew;
	private String crewNames;
	private List<EPCreateDTO> createDTO;
	
	public String getExctnPckgNam() {
		return exctnPckgNam;
	}
	public void setExctnPckgNam(String exctnPckgNam) {
		this.exctnPckgNam = exctnPckgNam;
	}
	public String getLeadCrew() {
		return leadCrew;
	}
	public void setLeadCrew(String leadCrew) {
		this.leadCrew = leadCrew;
	}
	public String getCrewNames() {
		return crewNames;
	}
	public void setCrewNames(String crewNames) {
		this.crewNames = crewNames;
	}
	public List<EPCreateDTO> getCreateDTO() {
		return createDTO;
	}
	public void setCreateDTO(List<EPCreateDTO> createDTO) {
		this.createDTO = createDTO;
	}
	
	

}
