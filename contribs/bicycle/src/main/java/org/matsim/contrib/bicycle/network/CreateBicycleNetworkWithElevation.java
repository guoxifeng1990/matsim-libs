package org.matsim.contrib.bicycle.network;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.api.core.v01.network.Node;
import org.matsim.contrib.bicycle.network.OsmBicycleReader;
import org.matsim.core.network.algorithms.MultimodalNetworkCleaner;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

import java.util.Set;

public class CreateBicycleNetworkWithElevation {

	//private static final String outputCRS = "EPSG:2154"; //UTM-32
	//private static final String outputCRS1 = "EPSG:4258"; //UTM-32
	//private static final String inputOsmFile = "C:\\Users\\fengg\\Desktop\\pt2matsim\\example\\ile-de-france-avantdeconfinement.osm.pbf";
	//private static final String inputTiffFile = "D:/paris_ele/paris.tif";
	//private static final String outputFile = "C:\\Users\\fengg\\Desktop/pt2matsim/example/paris_network.xml.gz";

    public static void main(String[] args) {

    	String outputCRS = args[0];
    	String inputOsmFile = args[1];
    	String inputTiffFile = args[2];
    	String outputFile = args[3];

        var elevationParser = new ElevationDataParser(inputTiffFile, outputCRS);
        var transformation = TransformationFactory.getCoordinateTransformation(TransformationFactory.WGS84, outputCRS);

        var network = new OsmBicycleReader.Builder()
                .setCoordinateTransformation(transformation)
                .setIncludeLinkAtCoordWithHierarchy((coord, hierachyLevel) -> ((hierachyLevel == 1 || hierachyLevel == 2) || (hierachyLevel == 3 || hierachyLevel == 4) || hierachyLevel == 5 || hierachyLevel == 6 || hierachyLevel == 7|| hierachyLevel == 8))
                .setAfterLinkCreated((link, tags, direction) -> {

                    addElevationIfNecessary(link.getFromNode(), elevationParser);
                    addElevationIfNecessary(link.getToNode(), elevationParser);
                })
                .build()
                .read(inputOsmFile);

        new MultimodalNetworkCleaner(network).run(Set.of(TransportMode.car));
        new MultimodalNetworkCleaner(network).run(Set.of(TransportMode.bike));
	    new MultimodalNetworkCleaner(network).run(Set.of("car_passenger"));


        new NetworkWriter(network).write(outputFile);
    }

    private static synchronized void addElevationIfNecessary(Node node, ElevationDataParser elevationParser) {

        if (!node.getCoord().hasZ()) {
            var elevation = elevationParser.getElevation(node.getCoord());
            var newCoord = CoordUtils.createCoord(node.getCoord().getX(), node.getCoord().getY(), elevation);
            // I think it should work to replace the coord on the node reference, since the network only stores references
            // to the node and the internal quad tree only references the x,y-values and the node. janek 4.2020
            node.setCoord(newCoord);
        }
    }
}
