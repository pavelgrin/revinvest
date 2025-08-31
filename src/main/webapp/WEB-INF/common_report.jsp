<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--@elvariable id="commonReport" type="net.grinv.revinvest.model.CommonReport"--%>
<%--@elvariable id="generationDate" type="java.lang.String"--%>

<div class="header">
    <div class="info">
        <div class="title">Profit and Loss Statement</div>
        <div class="summary">
            <div class="summaryRow">
                <div class="summaryLabel">Period</div>
                <div class="summaryValue">
                    <label for="dateFromInput" aria-label="Date From"></label>
                    <input
                        type="date"
                        id="dateFromInput"
                        required
                    >
                    <span>-</span>
                    <label for="dateToInput" aria-label="Date To"></label>
                    <input
                        type="date"
                        id="dateToInput"
                        required
                    >
                    <button id="dateUpdateButton">Update</button>
                </div>
            </div>
            <div class="summaryRow">
                <div class="summaryLabel">Currency</div>
                <div class="summaryValue">
                    <label for="currencySelect" aria-label="Select Currency"></label>
                    <select id="currencySelect">
                        <option value="USD">USD</option>
                        <option value="EUR">EUR</option>
                    </select>
                </div>
            </div>
            <div class="summaryRow">
                <div class="summaryLabel">Balance</div>
                <div class="summaryValue">${commonReport.balance}</div>
            </div>
            <div class="summaryRow">
                <div class="summaryLabel">Dividends</div>
                <div class="summaryValue">
                    gross: ${commonReport.dividends.withTax} /
                    net: ${commonReport.dividends.amount} /
                    tax: ${commonReport.dividends.tax}
                </div>
            </div>
            <div class="summaryRow">
                <div class="summaryLabel">Custody Fee</div>
                <div class="summaryValue">${commonReport.custodyFee}</div>
            </div>
            <div class="summaryRow">
                <div class="summaryLabel">Total (by sells)</div>
                <div class="summaryValue">
                    FIFO: ${commonReport.totalFIFO} /
                    LIFO: ${commonReport.totalLIFO}
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
    <c:forEach var="item" items="${commonReport.summaryFIFO}" varStatus="status">
    <tr>
        <td class="date">${item.date}</td>
        <td>
            <a symbol="${item.symbol}">${item.symbol}</a>
        </td>
        <td>${item.quantityFixed}</td>
        <td>${item.costBasis}</td>
        <td>${commonReport.summaryLIFO[status.index].costBasis}</td>
        <td>${item.grossProceeds}</td>
        <td>${item.pnl}</td>
        <td>${commonReport.summaryLIFO[status.index].pnl}</td>
    </tr>
    </c:forEach>
</table>
