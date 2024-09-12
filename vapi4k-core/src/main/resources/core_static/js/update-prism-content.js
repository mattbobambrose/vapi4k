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
        const element = document.querySelector(`#result-${divId}`);
        element.textContent = responseData;
        Prism.highlightElement(element);

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
        document.querySelectorAll('.sidebar-menu-item')
          .forEach(item => {
            item.classList.remove('active');
          });
        event.target.classList.add('active');
      }
    });
}

function selectTab(name) {
  document.querySelectorAll('.nav-link')
    .forEach(item => {
      item.classList.remove('active');
    });

  document.querySelector(`#${name}-tab`).classList.add('active');

  document.querySelectorAll('.validation-data')
    .forEach(item => {
      item.classList.add('hidden');
    });

  document.querySelector(`#${name}-data`).classList.remove('hidden');

}
