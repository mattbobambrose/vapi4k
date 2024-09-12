function updateMainPrismContent() {
  document.body.addEventListener(
    'htmx:afterOnLoad',
    function (event) {
      if (event.detail.target.id === `main-div`) {
        // Format the json result
        const codeElement = document.querySelector(`#result-main`);
        Prism.highlightElement(codeElement);
      }
    });
}

function updateToolPrismContent(divId) {
  document.body.addEventListener(
    'htmx:afterOnLoad',
    function (event) {
      if (event.detail.target.id === `result-${divId}`) {
        // Format the json result
        let responseData = event.detail.target.innerHTML;
        const codeElement = document.querySelector(`#result-${divId}`);
        codeElement.textContent = responseData;
        Prism.highlightElement(codeElement);

        // Make the json result visible
        const preElement = document.querySelector(`#display-${divId}`);
        preElement.style.display = 'block';
      }
    });
}

function updateSidebarSelected() {
  document.body.addEventListener(
    'click',
    function (event) {
      if (event.target.classList.contains('sidebar-menu-item')) {
        const navItems = document.querySelectorAll('.sidebar-menu-item');
        navItems.forEach(item => {
          item.classList.remove('active');
        });
        event.target.classList.add('active');
      }
    });
}
