package au.com.wp.corp.p6.dataservice.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import au.com.wp.corp.p6.dataservice.ResourceDetailDAO;
import au.com.wp.corp.p6.model.ResourceDetail;

@Repository
public class ResourceDetailDAOImpl implements ResourceDetailDAO {

	private volatile Map<String, List<String>> depotCrewMap = null;
	@Autowired 
	SessionFactory sessionFactory;
	
	@Override
	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, List<String>> fetchAllResourceDetail() {
		if(null == depotCrewMap){
			depotCrewMap = new HashMap<String, List<String>>();
			List<ResourceDetail> resourceDetails = (List<ResourceDetail>) getSession()
					.createCriteria(ResourceDetail.class)
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
			
			List<String> crewList = null;
			for (ResourceDetail resource:resourceDetails) {
				if(!depotCrewMap.containsKey(resource.getDepotNam())){
					crewList = new ArrayList<String>();
					crewList.add(resource.getRsrcNam());
					depotCrewMap.put(resource.getDepotNam(), crewList);
				}
				else{
					depotCrewMap.get(resource.getDepotNam()).add(resource.getRsrcNam());
				}
				
			}
		}
		return depotCrewMap;
	}

}
