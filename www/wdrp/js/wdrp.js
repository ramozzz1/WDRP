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
var selectTDGraphApiEndPoint = "select_tdgraph";
var getMapsApiEndPoint = "get_maps";
var getTDGraphsApiEndPoint = "get_tdgraphs";
var selectAlgorithmApiEndPoint = "select_algorithm";
var selectTDAlgorithmApiEndPoint = "select_tdalgorithm";
var selectWeatherApiEndPoint = "select_weather";
var getAlgorithmsApiEndPoint = "get_algorithms";
var getTDAlgorithmsApiEndPoint = "get_tdalgorithms";
var getWeathersApiEndPoint = "get_weathers";
var getWeatherLayerApiEndPoint = "get_weather_layer";
var features;
var beginTime;
var endTime;
var playingWeather;
var algorithmColors = {'CH': "#577c19", 'Dijkstra': "#534c96"}
var minDPTime = "17:00";
var maxDPTime = "17:05";

$(document).ready(function(){
	firstClickToRoute = true;
	playingWeather = false;
	
	//get maps available
	getMaps();
	
	//get maps available
	getTDGraphs();
	
	//get algorithms available
	getAlgorithms();
	
	//get td/algorithms available
	getTDAlgorithms();
	
	//get weather maps available
	getWeathers();
	
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
	
	setInterval(function(){
		if(playingWeather) {
			incrementWeatherTimeSlider();
		}
	},1500);
	
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

function getTDGraphs() {
	var url = "http://"+host+":"+port+"?";		
	url += "action=" + getTDGraphsApiEndPoint;
	console.log(url);
	$.ajax({
        url: url,
        type: "GET",
        dataType: "json", 
        error: function(err) {
            console.log(err);
        }, 
        success: function(json) {
        	var tdgraphs = json.tdgraphs;
        	var options = $("#tdGraphsList");
        	$.each(tdgraphs, function() {
        	    options.append($("<option />").val(this.fileName).text(this.fileName));
        	});
        }
    });
}

function getWeathers() {
	var url = "http://"+host+":"+port+"?";		
	url += "action=" + getWeathersApiEndPoint;
	console.log(url);
	$.ajax({
        url: url,
        type: "GET",
        dataType: "json", 
        error: function(err) {
            console.log(err);
        }, 
        success: function(json) {
        	var weathers = json.weathers;
        	console.log(weathers);
        	var options = $("#weathersList");
        	$.each(weathers, function() {
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
		url += "&minDPTime=" + minDPTime;
		//url += "&maxDPTime=" + maxDPTime;
		
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
	if(path.travel_time != -1) {
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
	else {
		console.log("No path found: " + path);
	}
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

function selectTDGraph() {
	var url = "http://"+host+":"+port+"?";		
	url += "action=" + selectTDGraphApiEndPoint;
	url += "&tdgraph_name=" + $("#tdGraphsList").val();
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

function getTDAlgorithms() {
	var url = "http://"+host+":"+port+"?";		
	url += "action=" + getTDAlgorithmsApiEndPoint;
	console.log(url);
	$.ajax({
        url: url,
        type: "GET",
        dataType: "json", 
        error: function(err) {
            console.log(err);
        }, 
        success: function(json) {
        	var tdalgorithms = json.tdalgorithms;
        	var options = $("#tdAlgorithmsList");
        	$.each(tdalgorithms, function() {
        	    options.append($("<option />").val(this.name).text(this.name));
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

function selectTDAlgorithm() {
	var url = "http://"+host+":"+port+"?";		
	url += "action=" + selectTDAlgorithmApiEndPoint;
	url += "&tdalgorithms=" + $("#tdAlgorithmsList").val();
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

function selectWeather() {
	var url = "http://"+host+":"+port+"?";		
	url += "action=" + selectWeatherApiEndPoint;
	url += "&weather_name=" + $("#weathersList").val();
	console.log(url);
	$.ajax({
        url: url,
        type: "GET",
        dataType: "json", 
        error: function(err) {
            console.log(err);
        }, 
        success: function(json) {
        	console.log("successfuly selected weather");
        	beginTime = new Date(json.beginTime);
        	endTime = new Date(json.endTime);
        	var diff = ((endTime - beginTime)/1000)/60;
        	$("#timeSlider").slider({
        	      value:0,
        	      min: 0,
        	      max: diff,
        	      step: json.timeStep,
        	      slide: function( event, ui ) {}
        	    });
        	
        	$( "#timeSlider" ).on("slide", 
        		function( event, ui ) {
        			updateWeatherAndTime(ui.value, false);
        		}
        	);
        	
        	updateWeatherAndTime(0, true);
        }
    });
}

function updateWeatherAndTime(value, zoom) {
	var currTime = valueToTime(value);
	$("#timeRange").html(currTime);
	showWeatherLayer(currTime, zoom);
}

function valueToTime(value) {
	var begin = new Date(beginTime.getTime()); 
	begin.setMinutes(begin.getMinutes() + value);
	return begin.toLocaleTimeString().slice(0,5);
}

function showWeatherLayer(time, zoom) {
	var url = "http://"+host+":"+port+"?";		
	url += "action=" + getWeatherLayerApiEndPoint;
	url += "&time=" + time;
	console.log(url);
	
	if(features) {
		for (var i = 0; i < features.length; i++)
			map.data.remove(features[i]);
	}
	
	$.ajax({
        url: url,
        type: "GET",
        dataType: "json", 
        error: function(err) {
            console.log(err);
        }, 
        success: function(json) {
        	features = map.data.addGeoJson(json);
        	if(zoom) zoomToWeahter();
        }
    });
}

function togglePlayWeather() {
	var inst = $( "#timeSlider" ).slider( "instance" );
	if(inst) {
		if(playingWeather) {
			playingWeather = false;
			$("#playButton").html("Play");
			setValueForSlider(0);
		}
		else {
			playingWeather = true;
			$("#playButton").html("Stop");
		}
	}
	else {
		alert("Select weather first");
	}	
}

function incrementWeatherTimeSlider() {
	var value = $("#timeSlider").slider( "option", "value" );
	var step = $("#timeSlider").slider( "option", "step" );
	var max = $("#timeSlider").slider( "option", "max" );
	var newValue = value+step;
	if(newValue>max)
		newValue = 0;
	
	setValueForSlider(newValue);
}

function setValueForSlider(val) {
	$("#timeSlider").slider("value", val);
	$("#timeSlider").trigger("slide", [{value:val},{}]);
}

function zoomToWeahter() {
  var bounds = new google.maps.LatLngBounds();
  map.data.forEach(function(feature) {
    processPoints(feature.getGeometry(), bounds.extend, bounds);
  });
  map.fitBounds(bounds);
  map.setZoom(map.getZoom()-2);
}

function processPoints(geometry, callback, thisArg) {
  if (geometry instanceof google.maps.LatLng) {
    callback.call(thisArg, geometry);
  } else if (geometry instanceof google.maps.Data.Point) {
    callback.call(thisArg, geometry.get());
  } else {
    geometry.getArray().forEach(function(g) {
      processPoints(g, callback, thisArg);
    });
  }
}