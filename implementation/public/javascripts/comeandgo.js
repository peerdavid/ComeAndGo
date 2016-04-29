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
});

$('.enable-editing-switch').click(function () {
    $('li.active form input').prop('disabled', !this.checked);
    $('li.active form button.ghost').css('display', (this.checked ? 'inline' : 'none'));
    //$('form select').attr('disabled', 'disabled');
});

/* fix for datepicker bug showing 19XX instead of 20XX */
$('.datepicker').focus(function () {
    var value = this.value.split('.');
    if(value.length < 3) {
        // initialize datepicker
        $('.datepicker').pickadate({
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
        });
    } else {
        // initialize datepicker
        $('.datepicker').pickadate({
            selectMonths: true,
            year: parseInt(value[2]) + 100,
            month: parseInt(value[1]),
            date: parseInt(value[0]),
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
        });
    }
});