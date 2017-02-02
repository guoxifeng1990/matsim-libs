/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2015 by the members listed in the COPYING,        *
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

package playground.ikaddoura;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.events.PersonMoneyEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.PersonEntersVehicleEventHandler;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.vehicles.Vehicle;

import com.google.inject.Inject;

/**
* @author ikaddoura
*/

public class PricingHandler implements PersonEntersVehicleEventHandler, LinkEnterEventHandler{
	private static final Logger log = Logger.getLogger(PricingHandler.class);

	@Inject
	private EventsManager events;
	
	private double amountSum = 0.;
	private Map<Id<Vehicle>, Id<Person>> vehicleId2personId = new HashMap<>();

	@Override
	public void reset(int iteration) {
		this.amountSum = 0.;
	}

	public double getAmountSum() {
		return amountSum;
	}

	@Override
	public void handleEvent(LinkEnterEvent event) {
		
		double amount = 0.;
		if (event.getLinkId().toString().equals("link_4_5")) {
			if (event.getVehicleId().toString().startsWith("lkw")) {
				amount = -12345.0;
			} else {
				amount = -3000.;
			}
		} else if (event.getLinkId().toString().equals("link_7_8")) {
			amount = -10.0;
		}
				
		PersonMoneyEvent moneyEvent = new PersonMoneyEvent(event.getTime(), this.vehicleId2personId.get(event.getVehicleId()), amount);
		this.events.processEvent(moneyEvent);	
		
		log.warn(moneyEvent.toString());
	}

	@Override
	public void handleEvent(PersonEntersVehicleEvent event) {
		vehicleId2personId.put(event.getVehicleId(), event.getPersonId());
	}
	
}

