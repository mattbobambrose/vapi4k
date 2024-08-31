function setupPrismUpdate(divId) {
  document.body.addEventListener(
    'htmx:afterOnLoad',
    function (event) {
      if (event.detail.target.id === `result-${divId}`) {
        // Highlight the json result
        let responseData = event.detail.target.innerHTML;
        const codeElement = document.querySelector(`#result-${divId}`);
        codeElement.textContent = responseData;
        Prism.highlightElement(codeElement);

        // Make the jsosn result visible
        const preElement = document.querySelector(`#display-${divId}`);
        preElement.style.display = 'block';
      }
    });
}
