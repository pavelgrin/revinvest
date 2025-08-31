<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--@elvariable id="baseReport" type="net.grinv.revinvest.model.BaseReport"--%>
<%--@elvariable id="tickerReport" type="net.grinv.revinvest.model.TickerReport"--%>
<%--@elvariable id="generationDate" type="java.lang.String"--%>

<div class="header">
    <div class="info">
        <div class="title">Profit and Loss Statement (${baseReport.symbol})</div>
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
                    <button id="dateUpdateButton">update</button>
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
                <div class="summaryLabel">Dividends</div>
                <div class="summaryValue">net: ${tickerReport.dividends}</div>
            </div>
            <div class="summaryRow">
                <div class="summaryLabel">Buy amount</div>
                <div class="summaryValue">${tickerReport.summary.buy}</div>
            </div>
            <div class="summaryRow">
                <div class="summaryLabel">Sell amount</div>
                <div class="summaryValue">${tickerReport.summary.sell}</div>
            </div>
            <div class="summaryRow">
                <div class="summaryLabel">Net amount</div>
                <div class="summaryValue">${tickerReport.summary.netAmount}</div>
            </div>
            <div class="summaryRow">
                <div class="summaryLabel">Quantity</div>
                <div class="summaryValue">${tickerReport.summary.quantityFixed}</div>
            </div>
            <div class="summaryRow">
                <div class="summaryLabel">Break-Even Point</div>
                <div class="summaryValue">${tickerReport.summary.bep}</div>
            </div>
            <div class="summaryRow">
                <div class="summaryLabel">Profit and Loss</div>
                <div class="summaryValue">LIFO: ${tickerReport.pnlTotal}</div>
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
    <c:forEach var="item" items="${tickerReport.sellsSummary}">
    <tr>
        <td class="date">${item.date}</td>
        <td>${item.quantityFixed}</td>
        <td>${item.costBasis}</td>
        <td>${item.grossProceeds}</td>
        <td>${item.pnl}</td>
    </tr>
    </c:forEach>
</table>
