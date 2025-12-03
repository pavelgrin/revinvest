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
                <div class="summaryValue">${report.commonReport.balance}</div>
            </div>
            <div class="summaryRow">
                <div class="summaryLabel">Dividends</div>
                <div class="summaryValue">
                    gross: ${report.commonReport.dividends.getWithTaxFixed} /
                    net: ${report.commonReport.dividends.getAmountFixed} /
                    tax: ${report.commonReport.dividends.getTaxFixed}
                </div>
            </div>
            <div class="summaryRow">
                <div class="summaryLabel">Custody Fee</div>
                <div class="summaryValue">${report.commonReport.custodyFee}</div>
            </div>
            <div class="summaryRow">
                <div class="summaryLabel">Total (by sells)</div>
                <div class="summaryValue">
                    FIFO: ${report.commonReport.totalFIFO} /
                    LIFO: ${report.commonReport.totalLIFO}
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
    <c:forEach var="item" items="${report.commonReport.summaryFIFO}" varStatus="status">
    <tr>
        <td class="date">${item.date}</td>
        <td>
            <a symbol="${item.symbol}">${item.symbol}</a>
        </td>
        <td>${item.quantityFixed}</td>
        <td>${item.costBasis}</td>
        <td>${report.commonReport.summaryLIFO[status.index].costBasis}</td>
        <td>${item.grossProceeds}</td>
        <td>${item.pnl}</td>
        <td>${report.commonReport.summaryLIFO[status.index].pnl}</td>
    </tr>
    </c:forEach>
</table>
