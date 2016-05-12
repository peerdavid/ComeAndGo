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
    updateNewNotificationBadge()    // Do it immediately
    setInterval(updateNewNotificationBadge, 1000);

    $('.enable-editing-switch').prop( "checked", false );

    // filter bar on edituser
    $(function(){
        listFilter($("#user-filter"),$("#user-list"));
    });

    // filter bar for not read notifications on notifications view
    $(function(){
        listFilter($("#not-read-filter"),$("#not-read-list"));
    });

});


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
    enableForm(this.checked);

});

function enableForm(enable) {
    $('li.active form input').prop('disabled', !enable);
    $('li.active form button.ghost').css('display', (enable ? 'inline' : 'none'));
}

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


// code from: https://kilianvalkhof.com/2010/javascript/how-to-build-a-fast-simple-list-filter-with-jquery/
// slightly modified
function listFilter(input,list) {

    $(input).change(function(){
        var filter = $(this).val();
        if(filter) {
            $(list).find("b:not(:Contains(" + filter + "))").parent().slideUp();
            $(list).find("b:Contains(" + filter + ")").parent().slideDown();
        } else {
            $(list).find("li").slideDown();
        }
    }).keyup(function(){
        $(this).change();
    });
    
};

jQuery.expr[':'].Contains = function(a,i,m){
    return (a.textContent||a.innerText||"").toUpperCase().indexOf(m[3].toUpperCase())>=0;
};
