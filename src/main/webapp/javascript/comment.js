$(function(){
    function bgAuto(){
		var bgW = $(window).width(),
			bgH = $(window).height();
		$("#desktop_bg img").css({"height":bgH,"width":bgW});
		var deskTopLi = $("#desktopIco li"),
		    icoLenth = deskTopLi.length,
		    icoArea = deskTopLi.outerHeight()*deskTopLi.outerWidth();
			icoAreaAll = icoArea*icoLenth,
			icoAllH = bgH-145;
			$('#desktopIco').width((icoAreaAll/icoAllH)+80);
			$("body").css({"height":bgH,"overflow":"hidden"});
			$("html").css({"height":bgH,"overflow":"hidden"});
	};
	bgAuto();
	//下拉列表
	var dowListBu = $("#dowListBu"),
	    dowLIist = $("#dowLIist"),
		dowItem = $("#dowLIist .dowItem");
		
		
	dowListBu.click(function(){
	    dowLIist.animate({"top":"37px"},300,function(){dowListBu.hide();});
	});
	
	dowListBu.trigger("click");
	
	dowLIist.click(function(event){
		var bgW = $(window).width(),
			bgH = $(window).height();
	    var eveTar = event.target;
		if(eveTar.className == "repeatX"){
			$(eveTar).parent("li").addClass("liOpen").siblings("li").removeClass("liOpen").removeAttr("style");
			var liOpen = $(".liOpen"),
			    liOpenP = $(eveTar).siblings("p");
				dowLiTitH = $('#dowLIist dl dt').height(),
				dowItemLen = $("#dowLIist .dowItem").length,
				dowItemH = $(eveTar).outerHeight(),
				dowItemAllH = dowItemH*dowItemLen,
				PH = bgH-dowLiTitH-dowItemAllH-300;
				liOpenP.css({'height':PH});
				
		}else if(eveTar.className == "ico titClose"){
				dowLIist.animate({"top":"-750px"},300,function(){dowListBu.show();});
		};
	});
	dowItem.first().children("h3").trigger("click");
	window.onresize = function(){
	    bgAuto();
		dowItem.first().children("h3").trigger("click");
	};
	/*任务栏*/
	function toolBRAuto(){
		var bgW = $(window).width(),
		    bgH = $(window).height(),
		    toolBR = $('#toolBR');
		    toolBRLi = $('#toolBR li:visible'),
		    tooLBRW = bgW-$('#toolBL').width(),
		    toolRLiLenth = toolBRLi.length,
			toolRLiAllW = 0;
			for(var i=0;i<=toolRLiLenth-1;i++){
			    toolRLiAllW += toolBRLi.eq(i).outerWidth();
			};
			if(toolRLiAllW>tooLBRW-150){
			    toolBR.addClass("listVertical").css({'height':toolRLiLenth*30});
				if($('.listVertical').height()>bgH/2){
				    $('.listVertical').css({'height': bgH/2, 'overflow': 'scroll','overflow-x':'hidden'})
				}
			}else{
			    toolBR.removeClass("listVertical").removeAttr('style');
			}
	};
	$(window).resize(toolBRAuto);
	/*弹框*/
	var alerWind = $("#alerBox01"),
		alerLeast = $("#alerLeast"),
		sharkBool = true,
	    alerArr = [],
		neAlerWin  = [],
		alerLeastArr = [];
	function alerBox (elemt){
		$("#alerLeast").remove();
		alerArr.push(elemt);
		var alerBoxNum = alerArr.length-1,
		    winH = $(window).height(),
			winW = $(window).width(),
		    alerWinH = winH/4*3,
			alerWinW = winW/4*3,
			alerTop = (winH-alerWinH)/2,
			alerLef = (winW-alerWinW)/2,
			
			alerBoxIcoSrc = $(alerArr[alerBoxNum]).children("img").attr("src"),
		    alerBoxIframSrc = $(alerArr[alerBoxNum]).attr("ahref"),
		    alerBoxIcoTit = $(alerArr[alerBoxNum]).children("span").text(),
			alerLeastChil = alerLeast.clone();
			alerLeastChil.attr({"id":"alerLeastChil"+alerBoxNum}).children("img").attr({"src":alerBoxIcoSrc});
			alerLeastChil.children(".icoTit").text(alerBoxIcoTit),
			newAlarWin = alerWind.clone();
			function alerWinDrag(){
			    $(this).css({'top': event.pageY, 'left': event.pageX})
			};
			
			function movTop (){
					   $("#newAlarWin"+(alerBoxNum)).css({'z-index':12}).show().siblings('.alerBox').css({'z-index':11}); 
			};
			function alerWinNor (){
					neAlerWin[alerBoxNum].css({'width':alerWinW,'height':alerWinH,'top':alerTop,'left':alerLef}).find("iframe").attr({"src":alerBoxIframSrc}).css({'width':alerWinW-12,'height':alerWinH-40});
			}; 
			function alerWinFul (){
					neAlerWin[alerBoxNum].css({'width':winW-10,'height':winH-10,'top':0,'left':0}).find("iframe").attr({"src":alerBoxIframSrc}).css({'width':winW-22,'height':winH-50});
			}; 
			newAlarWin.attr({"id":"newAlarWin"+alerBoxNum}).show("fast",alerWinNor);
			newAlarWin.children(".alerTit").children("span").text(alerBoxIcoTit);
			neAlerWin.push(newAlarWin);
			alerLeastArr.push(alerLeastChil);
			$("#alerBox01").before(neAlerWin[alerBoxNum].show().click(movTop));
			$("#toolBR").append(alerLeastArr[alerBoxNum].click(movTop).css({display:'block',opacity:0}).animate({opacity:1},300,toolBRAuto));
			
			neAlerWin[alerBoxNum].children(".alerTit").children(".titMin").click(function(event){
				event.stopPropagation();
			    $("#newAlarWin"+(alerBoxNum)).hide();
			});
			neAlerWin[alerBoxNum].children(".alerTit").children(".titClose").click(function(){
			    $("#newAlarWin"+(alerBoxNum)).remove();
				$("#alerLeastChil"+(alerBoxNum)).remove();
				toolBRAuto();
			});
			
			var nowAlerWin = neAlerWin[alerBoxNum];
			nowAlerWin.children(".alerTit").bind('mousedown',function(event){
				var alerBoxX01 = nowAlerWin.offset().left,
				    alerBoxY01 = nowAlerWin.offset().top,
					mouseX01 = event.pageX,
					mouseY01 = event.pageY;
				$(window).bind('mousemove',function(event){
					var _X = event.pageX-mouseX01,
					    _Y = event.pageY-mouseY01,
						nowX = alerBoxX01+_X,
						nowY = alerBoxY01+_Y;
				    nowAlerWin.css({'top': nowY, 'left': nowX});
				})
			});
			
			$(window).bind('mouseup',function(){
			    $(this).unbind('mousemove');
			});
			neAlerWin[alerBoxNum].children(".alerTit").children(".titMax").click(function(){
				if(sharkBool){
					alerWinFul();
					$(this).addClass('titShrink');
					sharkBool=!sharkBool;
				}else{
				    alerWinNor();
					$(this).removeClass('titShrink');
					sharkBool=!sharkBool;
				}
			});
			
	}
	
	$('#desktopIco').click(function (event){
	    var eveSurce = event.target,
		    eveNodeName = eveSurce.nodeName.toLowerCase();
		if(eveNodeName == "a" ||eveNodeName == "img" ||  eveNodeName == "span"){
			var isa = $(eveSurce).is("a"),
			    elemParen = $(eveSurce).parent();
			var eveSur = isa ? $(eveSurce) : elemParen;
			alerBox(eveSur);
		}
		
	});
	
	/*开始菜单*/
	function closMun(event){
		var eveSurce = $(event.target),
			boolParen = eveSurce.closest('#menuBor').length,
			boolView = $('#popMenuBor').is(':visible');
		if(!boolParen&&boolView){
			$('#toolBL .popMenuBor').hide();
			$(window).unbind('click',closMun);
		};
	}

	function tog(elemt){
			if(elemt.siblings(".popMenuBor").is(":hidden")){
				elemt.parent().parent().find(".popMenuBor").hide();
				elemt.siblings(".popMenuBor").css({"display":"block"});
					$(window).bind('click',closMun);
			}else{
				if(elemt.attr("id") == "menu"){
					$(".popMenuBor").hide();
					$(window).unbind('click',closMun);
				}else {
					elemt.siblings(".popMenuBor").css({"display":"none"});
				}
			}
	};
	
	
	$('#toolBot').click(function autoBind(event){
	    var eveSurce = event.target;
		if(eveSurce.nodeName == "A"){
		    if($(eveSurce).siblings(".popMenuBor")){
			    tog($(eveSurce));
			};
		};
	});
	

});
