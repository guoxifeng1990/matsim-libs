/* *********************************************************************** *
 * project: org.matsim.*
 * RandomSearchReplannerFactory.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2011 by the members listed in the COPYING,        *
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

package playground.wrashid.parkingSearch.withinday;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.router.TripRouter;
import org.matsim.withinday.mobsim.WithinDayEngine;
import org.matsim.withinday.replanning.replanners.interfaces.WithinDayDuringLegReplannerFactory;

import javax.inject.Provider;

public class RandomSearchReplannerFactory extends WithinDayDuringLegReplannerFactory {

	private final Scenario scenario;
	private final ParkingAgentsTracker parkingAgentsTracker;
	private final Provider<TripRouter> tripRouterFactory;

	public RandomSearchReplannerFactory(WithinDayEngine withinDayEngine, Scenario scenario, ParkingAgentsTracker parkingAgentsTracker,
										Provider<TripRouter> tripRouterFactory) {
		super(withinDayEngine);
		this.scenario = scenario;
		this.parkingAgentsTracker = parkingAgentsTracker;
		this.tripRouterFactory = tripRouterFactory;
	}

	@Override
	public RandomSearchReplanner createReplanner() {
		RandomSearchReplanner replanner = new RandomSearchReplanner(super.getId(), this.scenario, 
				this.getWithinDayEngine().getActivityRescheduler(), this.parkingAgentsTracker,
				this.tripRouterFactory.get());
		return replanner;
	}

}
