/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2014 by the members listed in the COPYING,        *
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

package org.matsim.contrib.taxi.optimizer.assignment;

import org.matsim.contrib.taxi.data.TaxiRequest;
import org.matsim.contrib.taxi.optimizer.TaxiOptimizerContext;


class AssignmentRequestData
    extends AssignmentDestinationData<TaxiRequest>
{
    private int urgentReqCount = 0;
    private final double currTime;
    private final double maxT0;


    AssignmentRequestData(TaxiOptimizerContext optimContext, double planningHorizon)
    {
        currTime = optimContext.timer.getTimeOfDay();
        maxT0 = currTime + planningHorizon;
    }


    @Override
    protected DestEntry<TaxiRequest> createEntry(int idx, TaxiRequest candidateDest)
    {
        double t0 = candidateDest.getT0();
        if (t0 > maxT0) {
            return null;
        }

        if (t0 <= currTime) {
            urgentReqCount++;
        }
        return new DestEntry<TaxiRequest>(idx, candidateDest, candidateDest.getFromLink(), t0);
    }


    public int getUrgentReqCount()
    {
        return urgentReqCount;
    }
}