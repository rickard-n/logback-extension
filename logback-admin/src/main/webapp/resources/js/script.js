function updateLog4J(logger, level, rowId) {
    $.ajax({
        type: 'POST',
        url: './loggers',
        data: 'logger='+logger+'&level='+level,
        success: function(){
            $(rowId).attr('class', level.toLowerCase());
        },
        error: function(){
            alert('Could not set log level.');
        }
    });
}