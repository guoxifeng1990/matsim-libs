/* *********************************************************************** *
 * project: org.matsim.*
 * PlanomatConfigGroupTest.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
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

package org.matsim.config.groups;

import org.matsim.basic.v01.BasicLeg;
import org.matsim.gbl.Gbl;
import org.matsim.testcases.MatsimTestCase;

public class PlanomatConfigGroupTest extends MatsimTestCase {

	public void testPlanomatConfigGroup() {
		
		super.loadConfig(this.getInputDirectory() + "empty_config.xml");
		
		assertEquals( PlanomatConfigGroup.DEFAULT_OPTIMIZATION_TOOLBOX, Gbl.getConfig().planomat().getOptimizationToolbox() );
		assertEquals( PlanomatConfigGroup.DEFAULT_POPSIZE, Gbl.getConfig().planomat().getPopSize() );
		assertEquals( PlanomatConfigGroup.DEFAULT_JGAP_MAX_GENERATIONS, Gbl.getConfig().planomat().getJgapMaxGenerations() );
		BasicLeg.Mode[] expectedModes = PlanomatConfigGroup.DEFAULT_POSSIBLE_MODES;
		BasicLeg.Mode[] actualModes = Gbl.getConfig().planomat().getPossibleModes();
		assertEquals(expectedModes.length, actualModes.length);
		for (int ii=0; ii < expectedModes.length; ii++) {
			assertEquals(expectedModes[ii], actualModes[ii]);
		}
		assertEquals( PlanomatConfigGroup.DEFAULT_LEG_TRAVEL_TIME_ESTIMATOR_NAME, Gbl.getConfig().planomat().getLegTravelTimeEstimatorName() );
	}

	public void testAddParam() {

		super.loadConfig(this.getInputDirectory() + "config.xml");

		BasicLeg.Mode[] expectedModes = new BasicLeg.Mode[]{BasicLeg.Mode.car, BasicLeg.Mode.pt};
		BasicLeg.Mode[] actualModes = Gbl.getConfig().planomat().getPossibleModes();
		
		assertEquals( expectedModes.length, actualModes.length);
		for (int ii=0; ii < expectedModes.length; ii++) {
			assertEquals(expectedModes[ii], actualModes[ii]);
		}
		assertEquals( PlanomatConfigGroup.CHARYPAR_ET_AL_COMPATIBLE, Gbl.getConfig().planomat().getLegTravelTimeEstimatorName() );
		
	}
	
}
