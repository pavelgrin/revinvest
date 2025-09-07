<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="currencies" value="<%= net.grinv.revinvest.consts.Currency.values() %>" scope="request" />
<%--@elvariable id="report" type="net.grinv.revinvest.model.Report"--%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Profit and Loss Statement</title>
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles.css">
</head>
<body>
    <div class="content">
        <jsp:include page="update_report.jsp" />
        <c:if test="${not empty report.tickerReport}">
            <jsp:include page="symbol_report.jsp" />
        </c:if>
        <c:if test="${not empty report.commonReport}">
            <jsp:include page="common_report.jsp" />
        </c:if>
    </div>
    <script>
        const __QUERY_PARAMS__ = Object.freeze({
            DateFrom: "<%= net.grinv.revinvest.consts.RequestParams.FROM %>",
            DateTo: "<%= net.grinv.revinvest.consts.RequestParams.TO %>",
            Symbol: "<%= net.grinv.revinvest.consts.RequestParams.SYMBOL %>",
            Currency: "<%= net.grinv.revinvest.consts.RequestParams.CURRENCY %>",
        });

        const __CURRENCY__ = "${report.filter.currency.code}"
        const __DATE_FROM__ = "${report.filter.from}"
        const __DATE_TO__ = "${report.filter.to}"
    </script>
    <script src="${pageContext.request.contextPath}/script.js"></script>
</body>
</html>
