/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2010 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.core.utils.misc;

import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.examples.ExamplesUtils;
import org.matsim.testcases.MatsimTestUtils;
import static org.hamcrest.CoreMatchers.*;

/**
 * @author mrieser
 */
public class ConfigUtilsTest {
	private static final Logger log = Logger.getLogger( ConfigUtilsTest.class ) ;

	@Rule
	public MatsimTestUtils util = new MatsimTestUtils();

	@Test
	public void testLoadConfig_filenameOnly() throws IOException {
		Config config = ConfigUtils.loadConfig(IOUtils.extendUrl(ExamplesUtils.getTestScenarioURL("equil"), "config.xml"));
		Assert.assertNotNull(config);
		Assert.assertEquals("network.xml", config.network().getInputFile());
	}

	@Test
	public void testLoadConfig_emptyConfig() throws IOException {
		Config config = new Config();
		Assert.assertNull(config.network());
		ConfigUtils.loadConfig(config, IOUtils.extendUrl(ExamplesUtils.getTestScenarioURL("equil"), "config.xml"));
		Assert.assertNotNull(config.network());
		Assert.assertEquals("network.xml", config.network().getInputFile());
	}

	@Test
	public void testLoadConfig_preparedConfig() throws IOException {
		Config config = new Config();
		config.addCoreModules();
		Assert.assertNotNull(config.network());
		Assert.assertNull(config.network().getInputFile());
		ConfigUtils.loadConfig(config, IOUtils.extendUrl(ExamplesUtils.getTestScenarioURL("equil"), "config.xml"));
		Assert.assertEquals("network.xml", config.network().getInputFile());
	}

	@Test
	public void testModifyPaths_missingSeparator() throws IOException {
		Config config = ConfigUtils.loadConfig(IOUtils.extendUrl(ExamplesUtils.getTestScenarioURL("equil"), "config.xml"));
		Assert.assertEquals("network.xml", config.network().getInputFile());
		ConfigUtils.modifyFilePaths(config, "/home/username/matsim");
		Assert.assertThat(config.network().getInputFile(), anyOf(is("/home/username/matsim/network.xml"),is("/home/username/matsim\\network.xml")));

	}

	@Test
	public void testModifyPaths_withSeparator() throws IOException {
		Config config = ConfigUtils.loadConfig(IOUtils.extendUrl(ExamplesUtils.getTestScenarioURL("equil"), "config.xml"));
		Assert.assertEquals("network.xml", config.network().getInputFile());
		ConfigUtils.modifyFilePaths(config, "/home/username/matsim/");
		Assert.assertThat(config.network().getInputFile(), anyOf(is("/home/username/matsim/network.xml"),is("/home/username/matsim\\network.xml")));
	}

	@Test
	public void loadConfigWithTypedArgs(){
		final URL url = IOUtils.extendUrl( ExamplesUtils.getTestScenarioURL( "equil" ), "config.xml" );
		final String [] typedArgs = {"--config:controler.outputDirectory=abc"} ;
		Config config = ConfigUtils.loadConfig( url, typedArgs );
		Assert.assertEquals("abc", config.controler().getOutputDirectory());
	}
	@Test
	public void loadConfigWithTypedArgsWithTypo(){
		boolean hasFailed = false ;
		try{
			final URL url = IOUtils.extendUrl( ExamplesUtils.getTestScenarioURL( "equil" ), "config.xml" );
			final String[] typedArgs = {"--config:controler.outputDirector=abc"};
			Config config = ConfigUtils.loadConfig( url, typedArgs );
			//		Assert.assertEquals("abc", config.controler().getOutputDirectory());
		} catch (Exception ee ){
			hasFailed = true ;
			log.warn("the above exception was expected") ;
		}
		Assert.assertTrue( hasFailed );
	}
}
