model = new Backbone.Model({
  	data:[
  		{
  			url : 'http://gmail.com/',
  		 	ruleset : [
  		 		{option : 'Enterprise', id : 'pramati' },
  		 		{option : 'Consumer', id : 'ySlow'}
  		 	],
  		 	overallGrade : "A",
  		 	overallPerformanceScore : 80,
  		 	activities :[
  		 	{
  		 		activityName : 'Scan',
  		 		activityId : 'scan',
  		 		activityGrade : 'E',
  		 		activityDetails : [
  		 			{ 
  		 				launchTime : 70, 
  		 				activityBackStack : 0, 
  		 				noOfOverdraws:6, 
  		 				gcCocurrent : 56, 
  		 				gcAlloc : 'not available',
  		 				activityDescription : "Code snippet for how to use Font Awesome on any element and a full list of all the Font Awesome icons and their CSS content values. Updated to v 4.0."
  		 			 }
  		 		]
  		 	},
  		 	{
  		 		activityName : 'Settings',
  		 		activityGrade : 'A',
  		 		activityId : 'settings',
  		 		activityDetails : [
  		 			{ 
  		 				launchTime : 54, 
  		 				activityBackStack : 10, 
  		 				noOfOverdraws: 'not available', 
  		 				gcCocurrent : 56, 
  		 				gcAlloc : 6,
  		 				activityDescription : "Code snippet for how to use Font Awesome on any element and a full list of all the Font Awesome icons and their CSS content values. Updated to v 4.0."
  		 			 }
  		 		]
  		 	}
  		 	]
  		 },
  	]
  	
  });
  
  var View = Backbone.View.extend({
  	
  	// initialzing 
  	initialize 	: 	function(){
  					this.render();
  					},
  	
  	//targetting DOM	
  	el : '#container',			
  	url 		: 	$('#urlText'),
  	grade 		: 	$('#grade'),
  	overallPerformanceScore : $('.performScoreNumber'),
  	activities : $('#activities'),
  	
  	// rendering on targeted DOM
  	render  	: 	function(){
			  	    
			  	    // getting Data from Model.data
			  		var data = this.model.get('data');
			  			data = data[0],
			  			overallGradeValue = data.overallGrade,
			  			overallGradeValueSmall  = overallGradeValue.toLowerCase(),
			  			overallPerformanceScoreValue = data.overallPerformanceScore;
			  			
			  		
			  		// setting data to targetted DOM
			  		this.url.text(data.url);
			  	
			  		for(var i = 0, l = data.ruleset.length; i < l; i++){
			  			var ruleSetOption = data.ruleset[i].option;
			  			this.$el.find('.selectpicker').append("<option value='update' data-subtext='old subtext'>"+ruleSetOption+"</option>");
			  		}
			  	    
			  		this.grade.text(overallGradeValue);
			  		this.grade.addClass('grade-'+overallGradeValueSmall);
			  		this.overallPerformanceScore.text(overallPerformanceScoreValue);
			  		
			  		for(var i = 0, l = data.activities.length; i<l; i++){
			  			var activityGradeValue = data.activities[i].activityGrade,
			  			activityGradeValueSmall = activityGradeValue.toLowerCase();
			  			this.$el.find('#activities ul').append("<li><a class='leftTabAction' href='#"+data.activities[i].activityId+"' data-toggle='tab'><div class='gradeBlock grade-"+activityGradeValueSmall+"'>"+activityGradeValue+"</div>"+data.activities[i].activityName+"</a></li>");			
			  			$('#activities ul li:first-child').addClass('active');
			  		}
			  		
  				},
  	
  	
  	events : {
  		'click .leftTabAction' : 'activityDescription',
  	},
  		
  	activityDescription : function(event){
      	var data = this.model.get('data');
			data = data[0];
			
    },
    
    
  });
  
  var view = new View({model : model});
    	  
  
// Applying Styles on the ruleset selection  
$(".selectpicker").selectpicker();

$('.global-search .search-start-button').on('click', function(){
	$(this).addClass('dNone');
		$(this).addClass('dNone');
		$('#container').removeClass('dNone');
		$('.app-name').removeClass('dNone');
});

function modifications() {
    // Create subtext
    $(".selectpicker > option:eq(0)").attr("data-subtext", "ySlow");

    // Modify subtext
    $(".selectpicker > option:eq(1)").attr("data-subtext", "Eye Log");

    // Delete subtext
    $(".selectpicker > option:eq(2)").removeAttr("Pramati");

    // Update control
    $(".selectpicker").selectpicker("refresh");
}

$('.container-wrapper-start-up').on('click', function(){
	$("#LoadingImage").show();
	$.ajax({
		url : "/profile/connected",
		success : function(result) {
			if(result === "true") {
				getPackages();
			} else {
				$("#LoadingImage").hide();
			}
		}
	});
});

function getPackages() {
	$.ajax({
		url : "/profile/packages",
		success : function(result) {
			if(result) {
				$('.container-wrapper-start-up').removeClass('show-this');
				$('.container-wrapper-start-up').addClass('hide-this');
				$('.container-packages').addClass('show-this');
				var concatHtml = "";
				for(var i = 0; i < result.length; i++) {
					concatHtml += "<p>" + result[i] + "</p><br/>"; 
				}
				$('.package-list')[0].innerHTML = concatHtml;
				$("#LoadingImage").hide();
			} else {
				$("#LoadingImage").hide();
			}
		}
	});
}

$('.search-start-button').on('click', function(){
  $("#LoadingImage").show();	
  var appName = $(".search-input-big")[0].value;
  $.ajax({
	type: "POST",
    url : "/profile/packages?packageName=" + appName,
    success : function() {
    	$('.container-packages').removeClass('show-this');
    	$('.container-packages').addClass('hide-this');
		$('.container-stop-profile').addClass('show-this');
		$("#LoadingImage").hide();
    }
  });
});

$('.stop-button').on('click', function(){
	$("#LoadingImage").show();
	$.ajax({
	    url : "/profile/stop",
	    success : function(data) {
	    	$('.container-stop-profile').removeClass('show-this');
	    	$('.container-stop-profile').addClass('hide-this');
	    	$('.container-profile-data').addClass('show-this');
	    	$('.profile-data')[0].innerHTML = "<pre>" + JSON.stringify(data, undefined, 2); + "</pre>";
	    	$("#LoadingImage").hide();
	    }
	});
});

