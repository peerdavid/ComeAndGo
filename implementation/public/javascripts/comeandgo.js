/**
 * Created by csaq5996 on 4/11/16.
 */

$( document ).ready(function() {
    $('.datepicker').pickadate({
        selectMonths: true, // Creates a dropdown to control month
        selectYears: 15 // Creates a dropdown of 15 years to control year
    });
    $(".button-collapse").sideNav();
    $('select').material_select();
    $('.modal-trigger').leanModal();
    $('#timepicker').timepicki({
        show_meridian:false,
        min_hour_value:0,
        max_hour_value:23,
        overflow_minutes:true,
        increase_direction:'up',
        disable_keyboard_mobile: true
    });
});

$('.edit-user-switch').click(function () {
    $('li.active form input').prop('disabled', !this.checked);
    $('li.active form button.ghost').css('display', (this.checked ? 'inline' : 'none'));
});