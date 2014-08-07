// Global variables; see the code below for their purpose.
var host = "localhost";
var port = 8888;
var map;
var source;
var target;
var lines;
var googleLine;
var firstClickToRoute;
var directionsService = new google.maps.DirectionsService();
var routeApiEndPoint = "route";
var graphBoundsApiEndPoint = "graph_bounds";
var edgesApiEndPoint = "edges";
var selectGraphApiEndPoint = "select_graph";
var getMapsApiEndPoint = "get_maps";
var selectAlgorithmApiEndPoint = "select_algorithm";
var getAlgorithmsApiEndPoint = "get_algorithms";
var algorithmColors = {'CH': "#577c19", 'Dijkstra': "#534c96"}

$(document).ready(function(){
	firstClickToRoute = true;
	
	//get maps available
	getMaps();
	
	//get algorithms available
	getAlgorithms();
	
	//set first option as selected
	var defaultMap = $("#mapList option:first").val();
	$("#mapList").val(defaultMap);
	
	var latlng = new google.maps.LatLng(49.289307, 6.907654);
	var mapOptions = {
			zoom: 15,
			center: latlng,
			mapTypeId: google.maps.MapTypeId.ROADMAP
		};
	
	map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);
	selectMap();
  
	lines = [];
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

function getMaps() {
	var url = "http://"+host+":"+port+"?";		
	url += "action=" + getMapsApiEndPoint;
	console.log(url);
	$.ajax({
        url: url,
        type: "GET",
        dataType: "json", 
        error: function(err) {
            console.log(err);
        }, 
        success: function(json) {
        	var maps = json.maps;
        	var options = $("#mapsList");
        	$.each(maps, function() {
        	    options.append($("<option />").val(this.fileName).text(this.fileName));
        	});
        }
    });
}

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

function drawEdges() {
	var url = "http://"+host+":"+port+"?";		
	url += "action=" + edgesApiEndPoint;
	console.log(url);
	$.ajax({
        url: url,
        type: "GET",
        dataType: "json", 
        error: function(err) {
            console.log(err);
        }, 
        success: function(json) {
        	console.log(json);
        	var edges = json.edges;
        	for ( var i = 0; i < edges.length; i++ ) {
        		var edge = edges[i]; 
        		var points = [
        		            new google.maps.LatLng(edge.nodeFrom.lat,edge.nodeFrom.lon),
        		            new google.maps.LatLng(edge.nodeTo.lat,edge.nodeTo.lon)
        		            ];
        		
        		var color = !edge.shortcut ? '#000000' : '#ff0000'; 
        			
        		var path = new google.maps.Polyline({
        		    path: points,
        		    geodesic: true,
        		    strokeColor: color,
        		    strokeOpacity: 1.0,
        		    strokeWeight: 1
        		  });
        		
        		path.setMap(map);
        	}
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
                console.error(err);
            }, 
            success: function(json) {
            	console.log(json);
            	var paths = json.paths;
            	$.each(paths, function() {
            		console.log(this);
            		drawPath(this);
            	});
            }
        });
	}
}

function drawGraphBounds() {
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
	if(lines) { lines.forEach(function(line) { line.setMap(null); }); }
}

function drawStraightLine() {
	if(source!=null && target!=null) {
		var path = [source.getPosition(), target.getPosition()];
		line.setPath(path);
	}
}

function drawPath(path) {
	var pathList = new Array();
	var bounds = new google.maps.LatLngBounds();
	var points = path.points;
	for(i=0;i<points.length;i++) {
		var latlng = new google.maps.LatLng(points[i][0], points[i][1]);
		pathList.push(latlng);
		bounds.extend(latlng);
	}
	var line = new google.maps.Polyline({map: map, path: pathList,
		  strokeColor: algorithmColors[path.algorithm], strokeWeight: 8, strokeOpacity: 0.5});
	lines[lines.length] = line; 
	map.fitBounds(bounds);
}

function selectMap() {
	var url = "http://"+host+":"+port+"?";		
	url += "action=" + selectGraphApiEndPoint;
	url += "&graph_name=" + $("#mapsList").val();
	console.log(url);
	$.ajax({
        url: url,
        type: "GET",
        dataType: "json", 
        error: function(err) {
            console.log(err);
        }, 
        success: function(json) {
        	drawGraphBounds();
        	drawEdges();
        }
    });
}

function getAlgorithms() {
	var url = "http://"+host+":"+port+"?";		
	url += "action=" + getAlgorithmsApiEndPoint;
	console.log(url);
	$.ajax({
        url: url,
        type: "GET",
        dataType: "json", 
        error: function(err) {
            console.log(err);
        }, 
        success: function(json) {
        	var algorithms = json.algorithms;
        	var options = $("#algorithmsList");
        	$.each(algorithms, function() {
        	    options.append($("<option />").val(this.algorithmName).text(this.algorithmName));
        	});
        }
    });
}

function selectAlgorithm() {
	var url = "http://"+host+":"+port+"?";		
	url += "action=" + selectAlgorithmApiEndPoint;
	url += "&algorithms=" + $("#algorithmsList").val();
	console.log(url);
	$.ajax({
        url: url,
        type: "GET",
        dataType: "json", 
        error: function(err) {
            console.log(err);
        }, 
        success: function(json) {
        	
        }
    });
}