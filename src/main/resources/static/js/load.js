var loadButton = $('#loadButton');
var amountInput = $('#amount');
var courseList = $('#course-list');
var sampleCourse = $('#courseListItemSample');
var stepikUrl = "https://stepik.org/";
var coverTemplate = "/images/logo.jpg";

loadButton.click(function () {
    coursesAmount = amountInput.val();
    loadButton.prop('disabled', true);
    loadCourses(coursesAmount);
});

function loadCourses(amount) {
    $('#warning').show();
    $.get("/load",
        {
            amount: amount
        }
    )
        .done(function (data) {
            courseList.empty();
            $('#error').hide();
            $.each(data, function (index) {
                addCourseToList(data[index]);
            });
        })
        .fail(function () {
            $('#error').show();
        })
        .always(function () {
            $('#warning').hide();
            loadButton.prop('disabled', false);
        })
}

function addCourseToList(course) {
    courseListItem = sampleCourse.clone();
    courseListItem.removeAttr('hidden');
    courseListItem.attr('id', 'course' + course.id);
    courseListItem.find('a').attr('href', stepikUrl + 'course/' + course.id);
    if (course.cover) {
        courseListItem.find('img').attr('src', stepikUrl + course.cover);
    }
    courseListItem.find('#title').text(course.title);
    courseListItem.find('#learnersCount').text(course.learnersCount);
    courseList.append(courseListItem);
}