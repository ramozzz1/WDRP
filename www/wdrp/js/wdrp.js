// Global variables; see the code below for their purpose.
var host = "localhost";
var port = 8888;
var map;
var source;
var target;
var line;
var googleLine;
var firstClickToRoute;
var directionsService = new google.maps.DirectionsService();
var routeApiEndPoint = "route";
var graphBoundsApiEndPoint = "graph_bounds";

// Main program code. The jQuery construct $(document).ready(function(){ ... }
// ensures that this is executed only when the page has been loaded.
$(document).ready(function(){
	firstClickToRoute = true;
	
	var latlng = new google.maps.LatLng(49.289307, 6.907654);
	var mapOptions = {
			zoom: 15,
			center: latlng,
			mapTypeId: google.maps.MapTypeId.ROADMAP
		};
	
	map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);
	getGraphBounds();
	
	line = new google.maps.Polyline({map: map, path: [],
		  strokeColor: "blue", strokeWeight: 8, strokeOpacity: 0.5});
  
	googleLine = new google.maps.Polyline({map: map, path: [],
		  strokeColor: "red", strokeWeight: 8, strokeOpacity: 0.5});
	
	google.maps.event.addListener(map, "click", function(e)
	{
		var marker = placeMarker(e.latLng);
	  
		if (firstClickToRoute) {
			firstClickToRoute = false;
			deleteOverlays();
			
			source = marker;
		} else {
			firstClickToRoute = true;
			target = marker;
			
			computePath();
			computeGoogleMapsPath();
		}
	});
});

function computeGoogleMapsPath() {
	var request = {
			origin:source.getPosition(),
			destination:target.getPosition(),
			travelMode: google.maps.TravelMode.DRIVING
		};
	
	directionsService.route(request, function(result, status) {
		if (status == google.maps.DirectionsStatus.OK) {
			var path = new Array();
			for (var i = 0, len = result.routes[0].overview_path.length;i < len; i++)
				path.push(result.routes[0].overview_path[i]);
			googleLine.setPath(path);
		}
	});
}

function computePath() {
	if(source!=null && target!=null) {
		var url = "http://"+host+":"+port+"?";
		
		url += "action=" + routeApiEndPoint;
		url += "&source=" + source.getPosition().lat() + "," + source.getPosition().lng();
		url += "&target=" + target.getPosition().lat() + "," + target.getPosition().lng();
		
		$.ajax({
            url: url,
            type: "GET",
            dataType: "json", 
            error: function(err) {
                console.log(err);
            }, 
            success: function(json) {
            	drawPath(json.path);
            }
        });
	}
}

function getGraphBounds() {
	var url = "http://"+host+":"+port+"?";		
	url += "action=" + graphBoundsApiEndPoint;
	
	$.ajax({
        url: url,
        type: "GET",
        dataType: "json", 
        error: function(err) {
            console.log(err);
        }, 
        success: function(json) {
        	var bounds = json.bounds;
        	map.fitBounds(new google.maps.LatLngBounds(new google.maps.LatLng(bounds.minLat,bounds.minLon), new google.maps.LatLng(bounds.maxLat,bounds.maxLon)));
        	
        	var boundsPath = new Array();
        	boundsPath.push(new google.maps.LatLng(bounds.minLat,bounds.minLon));
        	boundsPath.push(new google.maps.LatLng(bounds.minLat,bounds.maxLon));
        	boundsPath.push(new google.maps.LatLng(bounds.maxLat,bounds.maxLon));
        	boundsPath.push(new google.maps.LatLng(bounds.maxLat,bounds.minLon));
        	boundsPath.push(new google.maps.LatLng(bounds.minLat,bounds.minLon));
        	
        	new google.maps.Polyline({map: map, path: boundsPath, strokeColor: "green", strokeWeight: 10, strokeOpacity: 0.8});
        }
    });
}

function placeMarker(location) {
	marker = new google.maps.Marker({
		draggable: true,
		position: location,
		map: map
	});
	
	google.maps.event.addListener(marker, 'dragend', computePath);
	
	return marker;
}

function deleteOverlays() {
	if(source) { source.setMap(null); source = null;}
	if(target) { target.setMap(null); target = null;}
	if(line) line.setPath([]);
}

// Implementation on the client.
function drawStraightLine() {
	if(source!=null && target!=null) {
		var path = [source.getPosition(), target.getPosition()];
		line.setPath(path);
	}
}

// Function that is called when the server has sent its answer.
function drawPath(path) {
	var pathList = new Array();
	var bounds = new google.maps.LatLngBounds();
	for(i=0;i<path.length;i++) {
		var latlng = new google.maps.LatLng(path[i][0], path[i][1]);
		pathList.push(latlng);
		bounds.extend(latlng);
	}
	line.setPath(pathList);
	map.fitBounds(bounds);
}