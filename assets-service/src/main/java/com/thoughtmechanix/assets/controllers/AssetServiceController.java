package com.thoughtmechanix.assets.controllers;

import com.thoughtmechanix.assets.model.Asset;
import com.thoughtmechanix.assets.services.AssetService;
import com.thoughtmechanix.assets.config.ServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value="v1/organizations/{organizationId}/assets")
public class AssetServiceController {
    @Autowired
    private AssetService assetService;

    @Autowired
    private ServiceConfig serviceConfig;

    @Autowired
    private HttpServletRequest request;

    private static final Logger logger = LoggerFactory.getLogger(AssetServiceController.class);


    @RequestMapping(value="/",method = RequestMethod.GET)
    public List<Asset> getAssets( @PathVariable("organizationId") String organizationId) {

        return assetService.getAssetsByOrg(organizationId);
    }

    @RequestMapping(value="/{assetId}",method = RequestMethod.GET)
    public Asset getAssets( @PathVariable("organizationId") String organizationId,
                                @PathVariable("assetId") String assetId) {
        logger.debug("Found tmx-correlation-id in asset-service-controller: {} ", request.getHeader("tmx-correlation-id"));
        return assetService.getAsset(organizationId, assetId);
    }

    @RequestMapping(value="{assetId}",method = RequestMethod.PUT)
    public void updateAssets( @PathVariable("assetId") String assetId, @RequestBody Asset asset) {
        assetService.updateAsset(asset);
    }

    @RequestMapping(value="/",method = RequestMethod.POST)
    public void saveAssets(@RequestBody Asset asset) {
        assetService.saveAsset(asset);
    }

    @RequestMapping(value="{assetId}",method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAssets( @PathVariable("assetId") String assetId, @RequestBody Asset asset) {
         assetService.deleteAsset(asset);
    }
}
