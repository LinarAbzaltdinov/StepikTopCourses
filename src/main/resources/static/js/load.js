$('#loadButton').click(loadCourses($('#amount').val()))

function loadCourses(amount) {
    $('#warning').show();
    $.ajax({
        method: "GET",
        url: "/load",
        data: {amount: amount}
    })
        .success(function (data, textStatus, xhr) {
            $('#warning').hide();
            table = $('#results-table');

        });
}