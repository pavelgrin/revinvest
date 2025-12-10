<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--@elvariable id="report" type="net.grinv.revinvest.model.Report"--%>
<%--@elvariable id="generationDate" type="java.lang.String"--%>

<div class="header">
    <div class="info">
        <div class="title">Profit and Loss Statement (${report.filter.symbol})</div>
        <div class="summary">
            <jsp:include page="filter_form.jsp" />
            <div class="summaryRow">
                <div class="summaryLabel">Dividends</div>
                <div class="summaryValue">net: ${report.tickerReport.getDividendsFixed}</div>
            </div>
            <div class="summaryRow">
                <div class="summaryLabel">Buy amount</div>
                <div class="summaryValue">${report.tickerReport.getSummary.getBuyAmountFixed}</div>
            </div>
            <div class="summaryRow">
                <div class="summaryLabel">Sell amount</div>
                <div class="summaryValue">${report.tickerReport.getSummary.getSellAmountFixed}</div>
            </div>
            <div class="summaryRow">
                <div class="summaryLabel">Net amount</div>
                <div class="summaryValue">${report.tickerReport.getSummary.getNetAmountFixed}</div>
            </div>
            <div class="summaryRow">
                <div class="summaryLabel">Quantity</div>
                <div class="summaryValue">${report.tickerReport.getSummary.getQuantityFixed}</div>
            </div>
            <div class="summaryRow">
                <div class="summaryLabel">Break-Even Point</div>
                <div class="summaryValue">${report.tickerReport.getSummary.getBepFixed}</div>
            </div>
            <div class="summaryRow">
                <div class="summaryLabel">Profit and Loss</div>
                <div class="summaryValue">LIFO: ${report.tickerReport.getPnlTotalFixed}</div>
            </div>
        </div>
    </div>
    <div class="generationDate">Generated on the ${generationDate}</div>
</div>
<table>
    <tr>
        <th class="date">Date</th>
        <th>Quantity</th>
        <th>Cost Basis</th>
        <th>Gross Proceeds</th>
        <th>PnL</th>
    </tr>
    <c:forEach var="item" items="${report.tickerReport.getSellsSummary}">
    <tr>
        <td class="date">${item.date}</td>
        <td>${item.getQuantityFixed}</td>
        <td>${item.getCostBasisFixed}</td>
        <td>${item.getGrossProceedsFixed}</td>
        <td>${item.getPnlFixed}</td>
    </tr>
    </c:forEach>
</table>
