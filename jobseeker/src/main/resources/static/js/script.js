"use strict";
/* ==== Jquery Functions ==== */
(function ($) {

    /* ==== Tool Tip ==== */
    $('[data-toggle="tooltip"]').tooltip();


    /* ==== Testimonials Slider ==== */
    $(".testimonialsList").owlCarousel({
        loop: true,
        margin: 30,
        nav: false,
        responsiveClass: true,
        responsive: {
            0: {
                items: 1,
                nav: false
            },
            768: {
                items: 1,
                nav: false
            },
            1170: {
                items: 1,
                nav: false,
                loop: true
            }
        }
    });


    /* ==== Revolution Slider ==== */
    if ($('.tp-banner').length > 0) {
        $('.tp-banner').show().revolution({
            delay: 6000,
            startheight: 550,
            startwidth: 1140,
            hideThumbs: 1000,
            navigationType: 'none',
            touchenabled: 'on',
            onHoverStop: 'on',
            navOffsetHorizontal: 0,
            navOffsetVertical: 0,
            dottedOverlay: 'none',
            fullWidth: 'on'
        });
    }

})(jQuery);

$(document).ready(function () {

    var allInterestedPositions = $('.allInterestedPositions');
    $('#interested').show();

    $('#applyFilters').click(function () {
        var allLocations = $('.checkboxLocation');
        var allCompanies = $('.checkboxCompany');
        var locationList = [];
        var companiesList = [];

        for (var i = 0; i < allLocations.length; i++) {
            if (allLocations[i].checked == true) {
                locationList.push(allLocations[i]['id'])
            }
        }

        for (var i = 0; i < allCompanies.length; i++) {
            if (allCompanies[i].checked == true) {
                companiesList.push(allCompanies[i]['id'])
            }
        }

        var searchQuery = document.getElementById("searchQuery").value;
        var search = [];
        search.push(searchQuery);

        var minSalary = document.getElementById("minSalary").value;
        var maxSalary = document.getElementById("maxSalary").value;

        var salary = [];
        salary.push(minSalary);
        salary.push(maxSalary);


        /*<![CDATA[*/
        var path = /*[[@{/}]]*/'search';
        /*]]>*/

        $.ajax({
            type: 'POST',
            url: path,
            data: JSON.stringify({company: companiesList, location: locationList, search: search,salary:salary}),
            contentType: 'application/json;charset=utf-8',
            success: function (res) {
                location.href = "http://localhost:8081/joblisting"
            },
            error: function (res) {
                console.log(res);
                location.reload();
            }
        });
    });

    $('#applyFilters1').click(function () {
        var allLocations = $('.checkboxLocation');
        var allCompanies = $('.checkboxCompany');
        var locationList = [];
        var companiesList = [];

        for (var i = 0; i < allLocations.length; i++) {
            if (allLocations[i].checked == true) {
                locationList.push(allLocations[i]['id'])
            }
        }

        for (var i = 0; i < allCompanies.length; i++) {
            if (allCompanies[i].checked == true) {
                companiesList.push(allCompanies[i]['id'])
            }
        }

        var searchQuery = document.getElementById("searchQuery").value;
        var search = [];
        search.push(searchQuery);

        var minSalary = document.getElementById("minSalary").value;
        var maxSalary = document.getElementById("maxSalary").value;

        var salary = [];
        salary.push(minSalary);
        salary.push(maxSalary);

        /*<![CDATA[*/
        var path = /*[[@{/}]]*/'search';
        /*]]>*/

        $.ajax({
            type: 'POST',
            url: path,
            data: JSON.stringify({company: companiesList, location: locationList, search: search, salary: salary}),
            contentType: 'application/json;charset=utf-8',
            success: function (res) {
                location.href = "http://localhost:8081/joblisting"
            },
            error: function (res) {
                console.log(res);
                location.reload();
            }
        });
    });

});