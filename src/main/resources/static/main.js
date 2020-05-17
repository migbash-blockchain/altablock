$(function() {
    $("td[colspan=3]").find("div").hide();
    $("tr").click(function(event) {
        var $target = $(event.target);
        $target.closest("tr").next().find("div").slideToggle();
    });
});