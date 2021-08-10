$(document).ready(function() {

$("textarea").keydown(function(e){
    // Enter was pressed
    if (e.keyCode == 13)
    {
        // prevent default behavior
        e.preventDefault();
    }
});

});