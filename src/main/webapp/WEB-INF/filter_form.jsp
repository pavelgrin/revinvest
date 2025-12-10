<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--@elvariable id="currencies" type="net.grinv.revinvest.consts.Currency[]"--%>

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
            <c:forEach var="cur" items="${currencies}">
                <option value="${cur.code}">${cur.code}</option>
            </c:forEach>
        </select>
    </div>
</div>
