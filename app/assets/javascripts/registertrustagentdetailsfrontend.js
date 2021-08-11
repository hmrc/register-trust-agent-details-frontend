$(document).ready(function() {

$("textarea").keydown(function(e){
    // Shift + Enter was pressed
    if (e.keyCode == 13 && e.shiftKey)
    {
        e.preventDefault();
    } else if (e.keyCode == 13) {
        e.preventDefault();
        $(".govuk-button").trigger("click");
    }
});

});