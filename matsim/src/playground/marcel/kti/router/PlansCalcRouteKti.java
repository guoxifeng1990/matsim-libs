/* *********************************************************************** *
 * project: org.matsim.*
 * CalcSwissPtPlan.java
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

package playground.marcel.kti.router;

import java.util.List;

import org.matsim.api.basic.v01.Coord;
import org.matsim.api.basic.v01.TransportMode;
import org.matsim.core.network.NetworkLayer;
import org.matsim.core.population.ActivityImpl;
import org.matsim.core.population.LegImpl;
import org.matsim.core.population.routes.RouteWRefs;
import org.matsim.core.router.AStarLandmarks;
import org.matsim.core.router.PlansCalcRoute;
import org.matsim.core.router.costcalculators.FreespeedTravelTimeCost;
import org.matsim.core.router.util.PreProcessLandmarks;
import org.matsim.core.router.util.TravelCost;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.matrices.Entry;
import org.matsim.matrices.Matrix;
import org.matsim.world.Location;
import org.matsim.world.Layer;
import org.matsim.world.MappedLocation;

public class PlansCalcRouteKti extends PlansCalcRoute {

	private final static double WALK_SPEED = 3.0/3.6; // 3.0km/h --> m/s = speed of people walking to the next station from home (bee-line!)

	private final NetworkLayer network;
	private final Matrix ptTravelTimes;
	private final SwissHaltestellen haltestellen;
	private final Layer municipalities;

	public PlansCalcRouteKti(final NetworkLayer network, final PreProcessLandmarks preProcessData,
			final TravelCost costCalculator, final TravelTime timeCalculator,
			final Matrix ptTravelTimes, final SwissHaltestellen haltestellen, final Layer municipalities) {
		this(network, preProcessData, costCalculator, timeCalculator, new FreespeedTravelTimeCost(), ptTravelTimes, haltestellen, municipalities);
	}

	private PlansCalcRouteKti(final NetworkLayer network, final PreProcessLandmarks preProcessData,
			final TravelCost costCalculator, final TravelTime timeCalculator,
			final FreespeedTravelTimeCost timeCostCalc,
			final Matrix ptTravelTimes, final SwissHaltestellen haltestellen, final Layer municipalities) {
		super(network, new AStarLandmarks(network, preProcessData, costCalculator, timeCalculator),
				new AStarLandmarks(network, preProcessData, timeCostCalc, timeCostCalc));
		this.network = network;
		this.ptTravelTimes = ptTravelTimes;
		this.haltestellen = haltestellen;
		this.municipalities = municipalities;
	}

	@Override
	public double handleLeg(final LegImpl leg, final ActivityImpl fromAct, final ActivityImpl toAct, final double depTime) {
		if (TransportMode.pt.equals(leg.getMode())) {
			return handleSwissPtLeg(fromAct, leg, toAct);
		}
		return super.handleLeg(leg, fromAct, toAct, depTime);
	}

	public double handleSwissPtLeg(final ActivityImpl fromAct, final LegImpl leg, final ActivityImpl toAct) {
		Coord fromStop = this.haltestellen.getClosestLocation(fromAct.getCoord());
		Coord toStop = this.haltestellen.getClosestLocation(toAct.getCoord());

		final List<MappedLocation> froms = this.municipalities.getNearestLocations(fromStop);
		final List<MappedLocation> tos = this.municipalities.getNearestLocations(toStop);
		Location from = froms.get(0);
		Location to = tos.get(0);
		Entry traveltime = this.ptTravelTimes.getEntry(from, to);
		if (traveltime == null) {
			throw new RuntimeException("No entry found for " + from.getId() + " --> " + to.getId());
		}
		final double timeInVehicle = traveltime.getValue() * 60.0;
		final double beeLineWalkTime = CoordUtils.calcDistance(fromAct.getCoord(), toAct.getCoord()) / WALK_SPEED;

		final double walkDistance = CoordUtils.calcDistance(fromAct.getCoord(), fromStop) + CoordUtils.calcDistance(toAct.getCoord(), toStop);
		final double walkTime = walkDistance / WALK_SPEED;
//		System.out.println(from.getId() + " > " + to.getId() + ": " + timeInVehicle/60 + "min + " + (walkTime / 60) + "min (" + walkDistance + "m walk); beeLine: " + beeLineWalkTime/60 + "min walk");

		RouteWRefs newRoute;
		if (beeLineWalkTime < (timeInVehicle + walkTime)) {
			newRoute = this.network.getFactory().createRoute(TransportMode.walk, fromAct.getLink(), toAct.getLink());
			leg.setRoute(newRoute);
			newRoute.setTravelTime(beeLineWalkTime);
		} else {
			newRoute = this.network.getFactory().createRoute(TransportMode.pt, fromAct.getLink(), toAct.getLink());
			leg.setRoute(newRoute);
			newRoute.setTravelTime(timeInVehicle + walkTime);
		}
		leg.setTravelTime(newRoute.getTravelTime());
//		System.out.println("cmpr:\t" + Time.writeTime(oldRoute.getTravTime()) + "\t" + Time.writeTime(leg.getRoute().getTravTime()) + "\t" + beeLineWalkTime);
		return newRoute.getTravelTime();
	}

}
