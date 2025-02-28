/*!
 * @license
 * chartjs-plugin-zoom
 * http://chartjs.org/
 * Version: 0.7.5
 *
 *
 * Copyright 2019 Chart.js Contributors
 * Released under the MIT license
 * https://github.com/chartjs/chartjs-plugin-zoom/blob/master/LICENSE.md
 */
(function (global, factory) {
typeof exports === 'object' && typeof module !== 'undefined' ? module.exports = factory(require('chart.js'), require('hammerjs')) :
typeof define === 'function' && define.amd ? define(['chart.js', 'hammerjs'], factory) :
(global = global || self, global.ChartZoom = factory(global.Chart, global.Hammer));
}(this, function (Chart, Hammer) { 'use strict';

Chart = Chart && Chart.hasOwnProperty('default') ? Chart['default'] : Chart;
Hammer = Hammer && Hammer.hasOwnProperty('default') ? Hammer['default'] : Hammer;

var helpers = Chart.helpers;

// Take the zoom namespace of Chart
var zoomNS = Chart.Zoom = Chart.Zoom || {};

// Where we store functions to handle different scale types
var zoomFunctions = zoomNS.zoomFunctions = zoomNS.zoomFunctions || {};
var panFunctions = zoomNS.panFunctions = zoomNS.panFunctions || {};

Chart.Zoom.defaults = Chart.defaults.global.plugins.zoom = {
	pan: {
		enabled: false,
		mode: 'xy',
		speed: 20,
		threshold: 10
	},
	zoom: {
		enabled: false,
		mode: 'xy',
		sensitivity: 3,
		speed: 0.1
	}
};

function resolveOptions(chart, options) {
	var deprecatedOptions = {};
	if (typeof chart.options.pan !== 'undefined') {
		deprecatedOptions.pan = chart.options.pan;
	}
	if (typeof chart.options.pan !== 'undefined') {
		deprecatedOptions.zoom = chart.options.zoom;
	}
	var props = chart.$zoom;
	options = props._options = helpers.merge({}, [options, deprecatedOptions]);

	// Install listeners. Do this dynamically based on options so that we can turn zoom on and off
	// We also want to make sure listeners aren't always on. E.g. if you're scrolling down a page
	// and the mouse goes over a chart you don't want it intercepted unless the plugin is enabled
	var node = props._node;
	var zoomEnabled = options.zoom && options.zoom.enabled;
	var dragEnabled = options.zoom.drag;
	if (zoomEnabled && !dragEnabled) {
		node.addEventListener('wheel', props._wheelHandler);
	} else {
		node.removeEventListener('wheel', props._wheelHandler);
	}
	if (zoomEnabled && dragEnabled) {
		node.addEventListener('mousedown', props._mouseDownHandler);
		node.ownerDocument.addEventListener('mouseup', props._mouseUpHandler);
	} else {
		node.removeEventListener('mousedown', props._mouseDownHandler);
		node.removeEventListener('mousemove', props._mouseMoveHandler);
		node.ownerDocument.removeEventListener('mouseup', props._mouseUpHandler);
	}
}

function storeOriginalOptions(chart) {
	var originalOptions = chart.$zoom._originalOptions;
	helpers.each(chart.scales, function(scale) {
		if (!originalOptions[scale.id]) {
			originalOptions[scale.id] = helpers.clone(scale.options);
		}
	});
	helpers.each(originalOptions, function(opt, key) {
		if (!chart.scales[key]) {
			delete originalOptions[key];
		}
	});
}

/**
 * @param {string} mode can be 'x', 'y' or 'xy'
 * @param {string} dir can be 'x' or 'y'
 */
function directionEnabled(mode, dir) {
	if (mode === undefined) {
		return true;
	} else if (typeof mode === 'string') {
		return mode.indexOf(dir) !== -1;
	}

	return false;
}

function rangeMaxLimiter(zoomPanOptions, newMax) {
	if (zoomPanOptions.scaleAxes && zoomPanOptions.rangeMax &&
			!helpers.isNullOrUndef(zoomPanOptions.rangeMax[zoomPanOptions.scaleAxes])) {
		var rangeMax = zoomPanOptions.rangeMax[zoomPanOptions.scaleAxes];
		if (newMax > rangeMax) {
			newMax = rangeMax;
		}
	}
	return newMax;
}

function rangeMinLimiter(zoomPanOptions, newMin) {
	if (zoomPanOptions.scaleAxes && zoomPanOptions.rangeMin &&
			!helpers.isNullOrUndef(zoomPanOptions.rangeMin[zoomPanOptions.scaleAxes])) {
		var rangeMin = zoomPanOptions.rangeMin[zoomPanOptions.scaleAxes];
		if (newMin < rangeMin) {
			newMin = rangeMin;
		}
	}
	return newMin;
}

function zoomCategoryScale(scale, zoom, center, zoomOptions) {
	var labels = scale.chart.data.labels;
	var minIndex = scale.minIndex;
	var lastLabelIndex = labels.length - 1;
	var maxIndex = scale.maxIndex;
	var sensitivity = zoomOptions.sensitivity;
	var chartCenter = scale.isHorizontal() ? scale.left + (scale.width / 2) : scale.top + (scale.height / 2);
	var centerPointer = scale.isHorizontal() ? center.x : center.y;

	zoomNS.zoomCumulativeDelta = zoom > 1 ? zoomNS.zoomCumulativeDelta + 1 : zoomNS.zoomCumulativeDelta - 1;

	if (Math.abs(zoomNS.zoomCumulativeDelta) > sensitivity) {
		if (zoomNS.zoomCumulativeDelta < 0) {
			if (centerPointer >= chartCenter) {
				if (minIndex <= 0) {
					maxIndex = Math.min(lastLabelIndex, maxIndex + 1);
				} else {
					minIndex = Math.max(0, minIndex - 1);
				}
			} else if (centerPointer < chartCenter) {
				if (maxIndex >= lastLabelIndex) {
					minIndex = Math.max(0, minIndex - 1);
				} else {
					maxIndex = Math.min(lastLabelIndex, maxIndex + 1);
				}
			}
			zoomNS.zoomCumulativeDelta = 0;
		} else if (zoomNS.zoomCumulativeDelta > 0) {
			if (centerPointer >= chartCenter) {
				minIndex = minIndex < maxIndex ? minIndex = Math.min(maxIndex, minIndex + 1) : minIndex;
			} else if (centerPointer < chartCenter) {
				maxIndex = maxIndex > minIndex ? maxIndex = Math.max(minIndex, maxIndex - 1) : maxIndex;
			}
			zoomNS.zoomCumulativeDelta = 0;
		}
		scale.options.ticks.min = rangeMinLimiter(zoomOptions, labels[minIndex]);
		scale.options.ticks.max = rangeMaxLimiter(zoomOptions, labels[maxIndex]);
	}
}

function zoomNumericalScale(scale, zoom, center, zoomOptions) {
	var range = scale.max - scale.min;
	var newDiff = range * (zoom - 1);

	var centerPoint = scale.isHorizontal() ? center.x : center.y;
	var minPercent = (scale.getValueForPixel(centerPoint) - scale.min) / range;
	var maxPercent = 1 - minPercent;

	var minDelta = newDiff * minPercent;
	var maxDelta = newDiff * maxPercent;

	scale.options.ticks.min = rangeMinLimiter(zoomOptions, scale.min + minDelta);
	scale.options.ticks.max = rangeMaxLimiter(zoomOptions, scale.max - maxDelta);
}

function zoomTimeScale(scale, zoom, center, zoomOptions) {
	zoomNumericalScale(scale, zoom, center, zoomOptions);

	var options = scale.options;
	if (options.time) {
		if (options.time.min) {
			options.time.min = options.ticks.min;
		}
		if (options.time.max) {
			options.time.max = options.ticks.max;
		}
	}
}

function zoomScale(scale, zoom, center, zoomOptions) {
	var fn = zoomFunctions[scale.type];
	if (fn) {
		fn(scale, zoom, center, zoomOptions);
	}
}

/**
 * @param chart The chart instance
 * @param {number} percentZoomX The zoom percentage in the x direction
 * @param {number} percentZoomY The zoom percentage in the y direction
 * @param {{x: number, y: number}} focalPoint The x and y coordinates of zoom focal point. The point which doesn't change while zooming. E.g. the location of the mouse cursor when "drag: false"
 * @param {string} whichAxes `xy`, 'x', or 'y'
 * @param {number} animationDuration Duration of the animation of the redraw in milliseconds
 */
function doZoom(chart, percentZoomX, percentZoomY, focalPoint, whichAxes, animationDuration) {
	var ca = chart.chartArea;
	if (!focalPoint) {
		focalPoint = {
			x: (ca.left + ca.right) / 2,
			y: (ca.top + ca.bottom) / 2,
		};
	}

	var zoomOptions = chart.$zoom._options.zoom;

	if (zoomOptions.enabled) {
		storeOriginalOptions(chart);
		// Do the zoom here
		var zoomMode = typeof zoomOptions.mode === 'function' ? zoomOptions.mode({chart: chart}) : zoomOptions.mode;

		// Which axe should be modified when figers were used.
		var _whichAxes;
		if (zoomMode === 'xy' && whichAxes !== undefined) {
			// based on fingers positions
			_whichAxes = whichAxes;
		} else {
			// no effect
			_whichAxes = 'xy';
		}

		helpers.each(chart.scales, function(scale) {
			if (scale.isHorizontal() && directionEnabled(zoomMode, 'x') && directionEnabled(_whichAxes, 'x')) {
				zoomOptions.scaleAxes = 'x';
				zoomScale(scale, percentZoomX, focalPoint, zoomOptions);
			} else if (!scale.isHorizontal() && directionEnabled(zoomMode, 'y') && directionEnabled(_whichAxes, 'y')) {
				// Do Y zoom
				zoomOptions.scaleAxes = 'y';
				zoomScale(scale, percentZoomY, focalPoint, zoomOptions);
			}
		});

		if (animationDuration) {
			chart.update({
				duration: animationDuration,
				easing: 'easeOutQuad',
			});
		} else {
			chart.update(0);
		}

		if (typeof zoomOptions.onZoom === 'function') {
			zoomOptions.onZoom({chart: chart});
		}
	}
}

function panCategoryScale(scale, delta, panOptions) {
	var labels = scale.chart.data.labels;
	var lastLabelIndex = labels.length - 1;
	var offsetAmt = Math.max(scale.ticks.length, 1);
	var panSpeed = panOptions.speed;
	var minIndex = scale.minIndex;
	var step = Math.round(scale.width / (offsetAmt * panSpeed));
	var maxIndex;

	zoomNS.panCumulativeDelta += delta;

	minIndex = zoomNS.panCumulativeDelta > step ? Math.max(0, minIndex - 1) : zoomNS.panCumulativeDelta < -step ? Math.min(lastLabelIndex - offsetAmt + 1, minIndex + 1) : minIndex;
	zoomNS.panCumulativeDelta = minIndex !== scale.minIndex ? 0 : zoomNS.panCumulativeDelta;

	maxIndex = Math.min(lastLabelIndex, minIndex + offsetAmt - 1);

	scale.options.ticks.min = rangeMinLimiter(panOptions, labels[minIndex]);
	scale.options.ticks.max = rangeMaxLimiter(panOptions, labels[maxIndex]);
}

function panNumericalScale(scale, delta, panOptions) {
	var tickOpts = scale.options.ticks;
	var prevStart = scale.min;
	var prevEnd = scale.max;
	var newMin = scale.getValueForPixel(scale.getPixelForValue(prevStart) - delta);
	var newMax = scale.getValueForPixel(scale.getPixelForValue(prevEnd) - delta);
	// The time scale returns date objects so convert to numbers. Can remove at Chart.js v3
	newMin = newMin.valueOf ? newMin.valueOf() : newMin;
	newMax = newMax.valueOf ? newMax.valueOf() : newMax;
	var rangeMin = newMin;
	var rangeMax = newMax;
	var diff;

	if (panOptions.scaleAxes && panOptions.rangeMin &&
			!helpers.isNullOrUndef(panOptions.rangeMin[panOptions.scaleAxes])) {
		rangeMin = panOptions.rangeMin[panOptions.scaleAxes];
	}
	if (panOptions.scaleAxes && panOptions.rangeMax &&
			!helpers.isNullOrUndef(panOptions.rangeMax[panOptions.scaleAxes])) {
		rangeMax = panOptions.rangeMax[panOptions.scaleAxes];
	}

	if (newMin >= rangeMin && newMax <= rangeMax) {
		tickOpts.min = newMin;
		tickOpts.max = newMax;
	} else if (newMin < rangeMin) {
		diff = prevStart - rangeMin;
		tickOpts.min = rangeMin;
		tickOpts.max = prevEnd - diff;
	} else if (newMax > rangeMax) {
		diff = rangeMax - prevEnd;
		tickOpts.max = rangeMax;
		tickOpts.min = prevStart + diff;
	}
}

function panTimeScale(scale, delta, panOptions) {
	panNumericalScale(scale, delta, panOptions);

	var options = scale.options;
	if (options.time) {
		if (options.time.min) {
			options.time.min = options.ticks.min;
		}
		if (options.time.max) {
			options.time.max = options.ticks.max;
		}
	}
}

function panScale(scale, delta, panOptions) {
	var fn = panFunctions[scale.type];
	if (fn) {
		fn(scale, delta, panOptions);
	}
}

function doPan(chartInstance, deltaX, deltaY) {
	storeOriginalOptions(chartInstance);
	var panOptions = chartInstance.$zoom._options.pan;
	if (panOptions.enabled) {
		var panMode = typeof panOptions.mode === 'function' ? panOptions.mode({chart: chartInstance}) : panOptions.mode;

		helpers.each(chartInstance.scales, function(scale) {
			if (scale.isHorizontal() && directionEnabled(panMode, 'x') && deltaX !== 0) {
				panOptions.scaleAxes = 'x';
				panScale(scale, deltaX, panOptions);
			} else if (!scale.isHorizontal() && directionEnabled(panMode, 'y') && deltaY !== 0) {
				panOptions.scaleAxes = 'y';
				panScale(scale, deltaY, panOptions);
			}
		});

		chartInstance.update(0);

		if (typeof panOptions.onPan === 'function') {
			panOptions.onPan({chart: chartInstance});
		}
	}
}

function getXAxis(chartInstance) {
	var scales = chartInstance.scales;
	var scaleIds = Object.keys(scales);
	for (var i = 0; i < scaleIds.length; i++) {
		var scale = scales[scaleIds[i]];

		if (scale.isHorizontal()) {
			return scale;
		}
	}
}

function getYAxis(chartInstance) {
	var scales = chartInstance.scales;
	var scaleIds = Object.keys(scales);
	for (var i = 0; i < scaleIds.length; i++) {
		var scale = scales[scaleIds[i]];

		if (!scale.isHorizontal()) {
			return scale;
		}
	}
}

// Store these for later
zoomNS.zoomFunctions.category = zoomCategoryScale;
zoomNS.zoomFunctions.time = zoomTimeScale;
zoomNS.zoomFunctions.linear = zoomNumericalScale;
zoomNS.zoomFunctions.logarithmic = zoomNumericalScale;
zoomNS.panFunctions.category = panCategoryScale;
zoomNS.panFunctions.time = panTimeScale;
zoomNS.panFunctions.linear = panNumericalScale;
zoomNS.panFunctions.logarithmic = panNumericalScale;
// Globals for category pan and zoom
zoomNS.panCumulativeDelta = 0;
zoomNS.zoomCumulativeDelta = 0;

// Chartjs Zoom Plugin
var zoomPlugin = {
	id: 'zoom',

	afterInit: function(chartInstance) {

		chartInstance.resetZoom = function() {
			storeOriginalOptions(chartInstance);
			var originalOptions = chartInstance.$zoom._originalOptions;
			helpers.each(chartInstance.scales, function(scale) {

				var timeOptions = scale.options.time;
				var tickOptions = scale.options.ticks;

				if (originalOptions[scale.id]) {

					if (timeOptions) {
						timeOptions.min = originalOptions[scale.id].time.min;
						timeOptions.max = originalOptions[scale.id].time.max;
					}

					if (tickOptions) {
						tickOptions.min = originalOptions[scale.id].ticks.min;
						tickOptions.max = originalOptions[scale.id].ticks.max;
					}
				} else {

					if (timeOptions) {
						delete timeOptions.min;
						delete timeOptions.max;
					}

					if (tickOptions) {
						delete tickOptions.min;
						delete tickOptions.max;
					}
				}


			});

			chartInstance.update();
		};

	},

	beforeUpdate: function(chart, options) {
		resolveOptions(chart, options);
	},

	beforeInit: function(chartInstance, pluginOptions) {
		chartInstance.$zoom = {
			_originalOptions: {}
		};
		var node = chartInstance.$zoom._node = chartInstance.ctx.canvas;
		resolveOptions(chartInstance, pluginOptions);

		var options = chartInstance.$zoom._options;
		var panThreshold = options.pan && options.pan.threshold;

		chartInstance.$zoom._mouseDownHandler = function(event) {
			node.addEventListener('mousemove', chartInstance.$zoom._mouseMoveHandler);
			chartInstance.$zoom._dragZoomStart = event;
		};

		chartInstance.$zoom._mouseMoveHandler = function(event) {
			if (chartInstance.$zoom._dragZoomStart) {
				chartInstance.$zoom._dragZoomEnd = event;
				chartInstance.update(0);
			}
		};

		chartInstance.$zoom._mouseUpHandler = function(event) {
			if (!chartInstance.$zoom._dragZoomStart) {
				return;
			}

			node.removeEventListener('mousemove', chartInstance.$zoom._mouseMoveHandler);

			var beginPoint = chartInstance.$zoom._dragZoomStart;

			var offsetX = beginPoint.target.getBoundingClientRect().left;
			var startX = Math.min(beginPoint.clientX, event.clientX) - offsetX;
			var endX = Math.max(beginPoint.clientX, event.clientX) - offsetX;

			var offsetY = beginPoint.target.getBoundingClientRect().top;
			var startY = Math.min(beginPoint.clientY, event.clientY) - offsetY;
			var endY = Math.max(beginPoint.clientY, event.clientY) - offsetY;

			var dragDistanceX = endX - startX;
			var dragDistanceY = endY - startY;

			// Remove drag start and end before chart update to stop drawing selected area
			chartInstance.$zoom._dragZoomStart = null;
			chartInstance.$zoom._dragZoomEnd = null;

			if (dragDistanceX <= 0 && dragDistanceY <= 0) {
				return;
			}

			var chartArea = chartInstance.chartArea;

			var zoomOptions = chartInstance.$zoom._options.zoom;
			var chartDistanceX = chartArea.right - chartArea.left;
			var xEnabled = directionEnabled(zoomOptions.mode, 'x');
			var zoomX = xEnabled && dragDistanceX ? 1 + ((chartDistanceX - dragDistanceX) / chartDistanceX) : 1;

			var chartDistanceY = chartArea.bottom - chartArea.top;
			var yEnabled = directionEnabled(zoomOptions.mode, 'y');
			var zoomY = yEnabled && dragDistanceY ? 1 + ((chartDistanceY - dragDistanceY) / chartDistanceY) : 1;

			doZoom(chartInstance, zoomX, zoomY, {
				x: (startX - chartArea.left) / (1 - dragDistanceX / chartDistanceX) + chartArea.left,
				y: (startY - chartArea.top) / (1 - dragDistanceY / chartDistanceY) + chartArea.top
			}, undefined, zoomOptions.drag.animationDuration);

			if (typeof zoomOptions.onZoomComplete === 'function') {
				zoomOptions.onZoomComplete({chart: chartInstance});
			}
		};

		var _scrollTimeout = null;
		chartInstance.$zoom._wheelHandler = function(event) {
			var rect = event.target.getBoundingClientRect();
			var offsetX = event.clientX - rect.left;
			var offsetY = event.clientY - rect.top;

			var center = {
				x: offsetX,
				y: offsetY
			};

			var zoomOptions = chartInstance.$zoom._options.zoom;
			var speedPercent = zoomOptions.speed;

			if (event.deltaY >= 0) {
				speedPercent = -speedPercent;
			}
			doZoom(chartInstance, 1 + speedPercent, 1 + speedPercent, center);

			clearTimeout(_scrollTimeout);
			_scrollTimeout = setTimeout(function() {
				if (typeof zoomOptions.onZoomComplete === 'function') {
					zoomOptions.onZoomComplete({chart: chartInstance});
				}
			}, 250);

			// Prevent the event from triggering the default behavior (eg. Content scrolling).
			if (event.cancelable) {
				event.preventDefault();
			}
		};

		if (Hammer) {
			var mc = new Hammer.Manager(node);
			mc.add(new Hammer.Pinch());
			mc.add(new Hammer.Pan({
				threshold: panThreshold
			}));

			// Hammer reports the total scaling. We need the incremental amount
			var currentPinchScaling;
			var handlePinch = function(e) {
				var diff = 1 / (currentPinchScaling) * e.scale;
				var rect = e.target.getBoundingClientRect();
				var offsetX = e.center.x - rect.left;
				var offsetY = e.center.y - rect.top;
				var center = {
					x: offsetX,
					y: offsetY
				};

				// fingers position difference
				var x = Math.abs(e.pointers[0].clientX - e.pointers[1].clientX);
				var y = Math.abs(e.pointers[0].clientY - e.pointers[1].clientY);

				// diagonal fingers will change both (xy) axes
				var p = x / y;
				var xy;
				if (p > 0.3 && p < 1.7) {
					xy = 'xy';
				} else if (x > y) {
					xy = 'x'; // x axis
				} else {
					xy = 'y'; // y axis
				}

				doZoom(chartInstance, diff, diff, center, xy);

				var zoomOptions = chartInstance.$zoom._options.zoom;
				if (typeof zoomOptions.onZoomComplete === 'function') {
					zoomOptions.onZoomComplete({chart: chartInstance});
				}

				// Keep track of overall scale
				currentPinchScaling = e.scale;
			};

			mc.on('pinchstart', function() {
				currentPinchScaling = 1; // reset tracker
			});
			mc.on('pinch', handlePinch);
			mc.on('pinchend', function(e) {
				handlePinch(e);
				currentPinchScaling = null; // reset
				zoomNS.zoomCumulativeDelta = 0;
			});

			var currentDeltaX = null;
			var currentDeltaY = null;
			var panning = false;
			var handlePan = function(e) {
				if (currentDeltaX !== null && currentDeltaY !== null) {
					panning = true;
					var deltaX = e.deltaX - currentDeltaX;
					var deltaY = e.deltaY - currentDeltaY;
					currentDeltaX = e.deltaX;
					currentDeltaY = e.deltaY;
					doPan(chartInstance, deltaX, deltaY);
				}
			};

			mc.on('panstart', function(e) {
				currentDeltaX = 0;
				currentDeltaY = 0;
				handlePan(e);
			});
			mc.on('panmove', handlePan);
			mc.on('panend', function() {
				currentDeltaX = null;
				currentDeltaY = null;
				zoomNS.panCumulativeDelta = 0;
				setTimeout(function() {
					panning = false;
				}, 500);

				var panOptions = chartInstance.$zoom._options.pan;
				if (typeof panOptions.onPanComplete === 'function') {
					panOptions.onPanComplete({chart: chartInstance});
				}
			});

			chartInstance.$zoom._ghostClickHandler = function(e) {
				if (panning && e.cancelable) {
					e.stopImmediatePropagation();
					e.preventDefault();
				}
			};
			node.addEventListener('click', chartInstance.$zoom._ghostClickHandler);

			chartInstance._mc = mc;
		}
	},

	beforeDatasetsDraw: function(chartInstance) {
		var ctx = chartInstance.ctx;

		if (chartInstance.$zoom._dragZoomEnd) {
			var xAxis = getXAxis(chartInstance);
			var yAxis = getYAxis(chartInstance);
			var beginPoint = chartInstance.$zoom._dragZoomStart;
			var endPoint = chartInstance.$zoom._dragZoomEnd;

			var startX = xAxis.left;
			var endX = xAxis.right;
			var startY = yAxis.top;
			var endY = yAxis.bottom;

			if (directionEnabled(chartInstance.$zoom._options.zoom.mode, 'x')) {
				var offsetX = beginPoint.target.getBoundingClientRect().left;
				startX = Math.min(beginPoint.clientX, endPoint.clientX) - offsetX;
				endX = Math.max(beginPoint.clientX, endPoint.clientX) - offsetX;
			}

			if (directionEnabled(chartInstance.$zoom._options.zoom.mode, 'y')) {
				var offsetY = beginPoint.target.getBoundingClientRect().top;
				startY = Math.min(beginPoint.clientY, endPoint.clientY) - offsetY;
				endY = Math.max(beginPoint.clientY, endPoint.clientY) - offsetY;
			}

			var rectWidth = endX - startX;
			var rectHeight = endY - startY;
			var dragOptions = chartInstance.$zoom._options.zoom.drag;

			ctx.save();
			ctx.beginPath();
			ctx.fillStyle = dragOptions.backgroundColor || 'rgba(225,225,225,0.3)';
			ctx.fillRect(startX, startY, rectWidth, rectHeight);

			if (dragOptions.borderWidth > 0) {
				ctx.lineWidth = dragOptions.borderWidth;
				ctx.strokeStyle = dragOptions.borderColor || 'rgba(225,225,225)';
				ctx.strokeRect(startX, startY, rectWidth, rectHeight);
			}
			ctx.restore();
		}
	},

	destroy: function(chartInstance) {
		if (!chartInstance.$zoom) {
			return;
		}
		var props = chartInstance.$zoom;
		var node = props._node;

		node.removeEventListener('mousedown', props._mouseDownHandler);
		node.removeEventListener('mousemove', props._mouseMoveHandler);
		node.ownerDocument.removeEventListener('mouseup', props._mouseUpHandler);
		node.removeEventListener('wheel', props._wheelHandler);
		node.removeEventListener('click', props._ghostClickHandler);

		delete chartInstance.$zoom;

		var mc = chartInstance._mc;
		if (mc) {
			mc.remove('pinchstart');
			mc.remove('pinch');
			mc.remove('pinchend');
			mc.remove('panstart');
			mc.remove('pan');
			mc.remove('panend');
			mc.destroy();
		}
	}
};

Chart.plugins.register(zoomPlugin);

return zoomPlugin;

}));
