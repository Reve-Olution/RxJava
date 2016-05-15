$(function () {
		
			var $observable_btn = $("#observable_btn");
			var $observers_events = $("#observers_events");
			console.log($observers_events);
			var observableBtn = document.getElementById("observable_btn");
			var observableEvents = document.getElementById("obervers_events");
			
			
			
			document.getElementById("observable_btn")
				.addEventListener("click", 
					function () {
						console.log("Click from addEventListener");
						click("Click from addEventListener");
				});
		
			$observable_btn.click(function(){
				click("Click from jquery");
			});
			
			
			var source = Rx.DOM.click(observableBtn);

			var subscription = source.subscribe(
			    function (x) {
			        console.log('clicked!');
			    },
			    function (err) {
			        console.log('Error: ' + err);
			    },
			    function () {
			        console.log('Completed');
			    });
			
			
			var click  = function (text) {
			
				$observers_events.append(text).append("<br/>");
				console.log(text);
			};
			
			
			
		})