function strLimit(data, limit) {
    if ((data!=null) && (data.length > limit)){
        data = data.substr(0, limit);
        data += '...';
    }
    return data;
}

function markAsSpam(clickEvent, msgId) {
    markAs(clickEvent, msgId, 'spam', 'Sunteți sigur că doriți să marcați ca Spam?');
}

function markAsEmail(clickEvent, msgId) {
    markAs(clickEvent, msgId, 'email', 'Sunteți sigur că doriți să mutați înapoi în Email?');
}

function markAs(clickEvent, msgId, actionType, message) {
    customAlert(message, function (result) {
        if (result) {
            /*<![CDATA[*/
            var actionUrl = "/api/markAs/" + actionType + "/" + msgId;
            /*]]>*/

            console.log("mark as: " + actionUrl);

            $(clickEvent.target).attr('data-loading-text', '<i class="fa fa-circle-o-notch fa-spin"></i> Se incarca');
            $(clickEvent.target).button('loading');
            $.ajax({
                url: actionUrl,
                method: 'POST'
            }).done(function () {
                location.reload();
                $(clickEvent.target).button('reset');
            }).fail(function (e) {
                console.log("Action failed: ");
                console.log(e);
                $(clickEvent.target).button('reset');
            });
        }
    });
}