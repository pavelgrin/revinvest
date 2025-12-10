<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--@elvariable id="report" type="net.grinv.revinvest.model.Report"--%>
<%--@elvariable id="generationDate" type="java.lang.String"--%>

<div class="header">
    <div class="info">
        <div class="title">Profit and Loss Statement</div>
        <div class="summary">
            <jsp:include page="filter_form.jsp" />
            <div class="summaryRow">
                <div class="summaryLabel">Balance</div>
                <div class="summaryValue">${report.commonReport.getBalanceFixed}</div>
            </div>
            <div class="summaryRow">
                <div class="summaryLabel">Dividends</div>
                <div class="summaryValue">
                    gross: ${report.commonReport.getDividends.getWithTaxFixed} /
                    net: ${report.commonReport.getDividends.getAmountFixed} /
                    tax: ${report.commonReport.getDividends.getTaxFixed}
                </div>
            </div>
            <div class="summaryRow">
                <div class="summaryLabel">Custody Fee</div>
                <div class="summaryValue">${report.commonReport.getCustodyFeeFixed}</div>
            </div>
            <div class="summaryRow">
                <div class="summaryLabel">Total (by sells)</div>
                <div class="summaryValue">
                    FIFO: ${report.commonReport.getTotalFIFOFixed} /
                    LIFO: ${report.commonReport.getTotalLIFOFixed}
                </div>
            </div>
        </div>
    </div>
    <div class="generationDate">Generated on the ${generationDate}</div>
</div>
<table>
    <tr>
        <th rowspan="2" class="date">Date</th>
        <th rowspan="2">Symbol</th>
        <th rowspan="2">Quantity</th>
        <th colspan="2">Cost Basis</th>
        <th rowspan="2">Gross Proceeds</th>
        <th colspan="2">PnL</th>
    </tr>
    <tr>
        <th class="secondRowHeader">FIFO</th>
        <th class="secondRowHeader">LIFO</th>
        <th class="secondRowHeader">FIFO</th>
        <th class="secondRowHeader">LIFO</th>
    </tr>
    <c:forEach var="item" items="${report.commonReport.getSummaryFIFO}" varStatus="status">
    <tr>
        <td class="date">${item.date}</td>
        <td>
            <a symbol="${item.symbol}">${item.symbol}</a>
        </td>
        <td>${item.getQuantityFixed}</td>
        <td>${item.getCostBasisFixed}</td>
        <td>${report.commonReport.getSummaryLIFO[status.index].getCostBasisFixed}</td>
        <td>${item.getGrossProceedsFixed}</td>
        <td>${item.getPnlFixed}</td>
        <td>${report.commonReport.getSummaryLIFO[status.index].getPnlFixed}</td>
    </tr>
    </c:forEach>
</table>
