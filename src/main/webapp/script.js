// -----------------------------------------------------------------------------
// Handle query building
const dateFromInput = document.getElementById("dateFromInput")
const dateToInput = document.getElementById("dateToInput")
const dateUpdateButton = document.getElementById("dateUpdateButton")

const currencySelect = document.getElementById("currencySelect")

const symbolLinks = document.querySelectorAll("[symbol]")

dateFromInput.value = __DATE_FROM__
dateToInput.value = __DATE_TO__
currencySelect.value = __CURRENCY__

const commonUrl = new URL(location.href)
const symbolUrl = new URL(location.href)

symbolLinks.forEach((link) => {
    symbolUrl.searchParams.set(
        __QUERY_PARAMS__.Symbol,
        link.getAttribute(__QUERY_PARAMS__.Symbol)
    )
    link.href = symbolUrl.href
})

dateUpdateButton.addEventListener("click", () => {
    if (currencySelect.value) {
        commonUrl.searchParams.set(__QUERY_PARAMS__.Currency, currencySelect.value)
    } else {
        commonUrl.searchParams.delete(__QUERY_PARAMS__.Currency)
    }
    if (dateFromInput.value) {
        commonUrl.searchParams.set(__QUERY_PARAMS__.DateFrom, dateFromInput.value)
    } else {
        commonUrl.searchParams.delete(__QUERY_PARAMS__.DateFrom)
    }
    if (dateToInput.value) {
        commonUrl.searchParams.set(__QUERY_PARAMS__.DateTo, dateToInput.value)
    } else {
        commonUrl.searchParams.delete(__QUERY_PARAMS__.DateTo)
    }
    location.href = commonUrl.href
})
// -----------------------------------------------------------------------------

// -----------------------------------------------------------------------------
// Handle statement uploading
const fileInput = document.getElementById("fileInput")
const fileUploadButton = document.getElementById("fileUploadButton")

const responseCaptionWaiting = document.getElementById(
    "responseCaptionWaiting"
)
const responseCaptionOk = document.getElementById("responseCaptionOk")
const responseCaptionError = document.getElementById(
    "responseCaptionError"
)

if (localStorage.getItem("uploaded")) {
    responseCaptionWaiting.style.opacity = 0
    responseCaptionError.style.opacity = 0
    responseCaptionOk.style.opacity = 1

    localStorage.removeItem("uploaded")
}

fileUploadButton.addEventListener("click", async () => {
    responseCaptionWaiting.style.opacity = 0
    responseCaptionOk.style.opacity = 0
    responseCaptionError.style.opacity = 0

    if (!fileInput.files.length) {
        responseCaptionError.style.opacity = 1
        return
    }

    responseCaptionWaiting.style.opacity = 1

    const formData = new FormData()
    formData.append("statement", fileInput.files[0])
    fileInput.value = ""

    try {
        const res = await fetch("/update", { method: "POST", body: formData })

        if (res.ok) {
            localStorage.setItem("uploaded", true)
            location.href = commonUrl.href
        } else {
            throw new Error("Updating error")
        }
    } catch(e) {
        responseCaptionWaiting.style.opacity = 0
        responseCaptionError.style.opacity = 1
    }
})
// -----------------------------------------------------------------------------
