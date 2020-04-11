package com.thoughtmechanix.organization.services;

import com.thoughtmechanix.organization.events.source.SimpleSourceBean;
import com.thoughtmechanix.organization.exception.ResourceNotFoundException;
import com.thoughtmechanix.organization.model.Organization;
import com.thoughtmechanix.organization.repository.OrganizationRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrganizationService {
    @Autowired
    private OrganizationRepository orgRepository;

    @Autowired
    SimpleSourceBean simpleSourceBean;

	private static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);

    public Organization getOrg(String organizationId) {
        return orgRepository.findById(organizationId);
    }

    public void saveOrg(Organization org){
        org.setId( UUID.randomUUID().toString());

        orgRepository.save(org);
        simpleSourceBean.publishOrgChange("SAVE", org.getId());
    }

    public void updateOrg(String organizationId, Organization orgRequest) throws ResourceNotFoundException {
    	Organization org = orgRepository.findById(organizationId);
    	if (org != null) {
    		logger.info("Found organization with id: {}", organizationId);

    		org.setName(orgRequest.getName());
    		org.setContactName(orgRequest.getContactName());
    		org.setContactEmail(orgRequest.getContactEmail());
    		org.setContactPhone(orgRequest.getContactPhone());

    		orgRepository.save(org);
    		simpleSourceBean.publishOrgChange("UPDATE", org.getId());
    	} else {
    		throw new ResourceNotFoundException("No organization found with id="+organizationId);
    	}
    }

    public void deleteOrg(String organizationId) throws ResourceNotFoundException {
    	Organization org = orgRepository.findById(organizationId);
    	if (org != null) {
    		logger.info("Found organization with id: {}", organizationId);
    		orgRepository.delete( organizationId );
    		simpleSourceBean.publishOrgChange("DELETE", organizationId);
    	} else {
    		throw new ResourceNotFoundException("No organization found with id="+organizationId);
    	}
    }
}
