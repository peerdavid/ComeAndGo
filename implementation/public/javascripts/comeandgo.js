/**
 * Created by csaq5996 on 4/11/16.
 */

$( document ).ready(function() {
    $(".button-collapse").sideNav();
    $('select').material_select();
    $('.modal-trigger').leanModal();
    $('.timepicker').pickatime({
        autoclose: true,
        twelvehour: false,
        ampmclickable: false,
        container: 'body'
    });

    // Poll new notifications every second
    updateNewNotificationBadge();   // Do it immediately
    setInterval(updateNewNotificationBadge, 1000);

    updateUserSate();
    setInterval(updateUserSate, 10000);

    $('.enable-editing-switch').prop( "checked", false );

    // filter bar on edituser
    $(function(){
        listFilter($("#user-filter"),$("#user-list"));
    });

    // filter bar for not read notifications on notifications view
    $(function(){
        listFilter($("#notification-filter"),$(".notification-list"));
    });

    enableEditing(false, null);

});

/*
 * get current state of user by ajax call
 */
var updateUserSate = function(){
    $.ajax({
        type:  'GET',
        contentType: 'application/json',
        data: '',
        url: '/state',
        success: function(data, textStatus, jqXHR) {
            if(data <= 0){
                return;
            }

            var json = JSON.parse(data);
            $('#state-message').text(json.message);

            var buttonCome = $('#button-come');
            var buttonGo = $('#button-go');
            var buttonStartBreak = $('#button-start-break');
            var buttonStopBreak = $('#button-stop-break');

            if(json.state === "active") {
                buttonGo.prop("disabled", false);
                buttonStartBreak.prop("disabled", false);
                buttonStartBreak.css("display", "inherit");
                buttonStopBreak.prop("disabled", true);
                buttonStopBreak.css("display", "none");
                buttonCome.prop("disabled", true);
            }
            else if(json.state === "inactive") {
                buttonGo.prop("disabled", true);
                buttonStartBreak.prop("disabled", true);
                buttonStartBreak.css("display", "inherit");
                buttonStopBreak.prop("disabled", true);
                buttonStopBreak.css("display", "none");
                buttonCome.prop("disabled", false);
            }
            else if(json.state === "pause") {
                buttonGo.prop("disabled", true);
                buttonStartBreak.prop("disabled", true);
                buttonStartBreak.css("display", "none");
                buttonStopBreak.prop("disabled", false);
                buttonStopBreak.css("display", "inherit");
                buttonCome.prop("disabled", true);
            }
        },
        error:function(jqXHR, textStatus, errorThrown) {
            console.log("State update failed " + textStatus);
        }
    });
};

/*
 * get notification count by ajax call
 */
var updateNewNotificationBadge = function(){
    $.ajax({
        type:  'GET',
        contentType: 'application/json',
        data: '',
        url: '/notification/number',
        success: function(data, textStatus, jqXHR) {
            if(data <= 0){
                return;
            }

            $('#span-new-notifcations')
                .addClass("new")
                .addClass("badge")
                .text(data);
        },
        error:function(jqXHR, textStatus, errorThrown) {
            console.log("Notification number request failed " + textStatus);
        }
    });
};

$('.enable-editing-switch').click(function () {
    enableEditing(true, this);
});

var enableEditing = function(enable, toggle) {
    if(toggle == null) toggle = $('.enable-editing-switch');
    var li = $(toggle).closest('li');
    li.find('form input').not('.modal-break input').prop('disabled', !toggle.checked);
    li.find('form button.ghost').css('display', (toggle.checked ? 'inline' : 'none'));
};

/* fix for datepicker bug showing 19XX instead of 20XX */
$('.datepicker').focus(function () {
    var value = this.value.split('.');

    var options = {
        selectMonths: true,
        selectYears: 10,
        // translation strings
        monthsFull: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'],
        monthsShort: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
        weekdaysFull: ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'],
        weekdaysShort: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
        showMonthsShort: true,
        showWeekdaysFull: false,
        format: 'dd.mm.yyyy',
        container: 'body'
    };

    // Its a date, if there are more than 2 dots
    if(value.length > 2) {
        options.year = parseInt(value[2]) + 100;
        options.month = parseInt(value[1]);
        options.date = parseInt(value[0]);
    }

    // initialize datepicker
    $('.datepicker').pickadate(options);
});

$('.notification-filter-checkbox').change(function () {
    var listings = $('.notification-list');
    var selector = '';

    if($('#filter-notread').prop('checked')) {
        selector += '#not-read-list'
    }
    if($('#filter-read').prop('checked')) {
        if(selector != '') selector += ',';
        selector += '#read-list'
    }
    if($('#filter-sent').prop('checked')) {
        if(selector != '') selector += ',';
        selector += '#sent-list'
    }
    $(function(){
        listFilter($("#notification-filter"), $(selector));
    });
});


// used to filter list
// code slightly modified from:
// https://kilianvalkhof.com/2010/javascript/how-to-build-a-fast-simple-list-filter-with-jquery/
function listFilter(input,list) {
    if(input.size() == 0) return;
    if(listFilter.filterFunction != undefined) {
        $(input).unbind('change', listFilter.filterFunction).bind('keyup', listFilter.filterFunction);
        for(var i = 0; i < listFilter.list.length; ++i) {
            $(listFilter.list[i]).find("b").parent().slideDown();
        }
    }

    listFilter.list = list;
    listFilter.filterFunction = function () {
        listFilter.filter = $(input).val();
        for(var i = 0; i < listFilter.list.length; ++i) {
            $(listFilter.list[i]).find("b:not(:Contains(" + listFilter.filter + "))").parent().slideUp();
            $(listFilter.list[i]).find("b:Contains(" + listFilter.filter + ")").parent().slideDown();
        }
    };
    listFilter.changeFunction = function () {

    };
    $(input).bind('change', listFilter.filterFunction).bind('keyup', listFilter.filterFunction);
    listFilter.filterFunction();
};

// diffrent contains function for case insensitivity
jQuery.expr[':'].Contains = function(a,i,m){
    return (a.textContent||a.innerText||"").toUpperCase().indexOf(m[3].toUpperCase())>=0;
};
