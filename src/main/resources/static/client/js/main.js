(function ($) {
  "use strict";

  // Spinner
  var spinner = function () {
    setTimeout(function () {
      if ($("#spinner").length > 0) {
        $("#spinner").removeClass("show");
      }
    }, 1);
  };
  spinner(0);

  // Fixed Navbar
  $(window).scroll(function () {
    if ($(window).width() < 992) {
      if ($(this).scrollTop() > 55) {
        $(".fixed-top").addClass("shadow");
      } else {
        $(".fixed-top").removeClass("shadow");
      }
    } else {
      if ($(this).scrollTop() > 55) {
        $(".fixed-top").addClass("shadow").css("top", 0);
      } else {
        $(".fixed-top").removeClass("shadow").css("top", 0);
      }
    }
  });

  // Back to top button
  $(window).scroll(function () {
    if ($(this).scrollTop() > 300) {
      $(".back-to-top").fadeIn("slow");
    } else {
      $(".back-to-top").fadeOut("slow");
    }
  });
  $(".back-to-top").click(function () {
    $("html, body").animate({ scrollTop: 0 }, 1500, "easeInOutExpo");
    return false;
  });

  // Testimonial carousel
  $(".testimonial-carousel").owlCarousel({
    autoplay: true,
    smartSpeed: 2000,
    center: false,
    dots: true,
    loop: true,
    margin: 25,
    nav: true,
    navText: [
      '<i class="bi bi-arrow-left"></i>',
      '<i class="bi bi-arrow-right"></i>',
    ],
    responsiveClass: true,
    responsive: {
      0: {
        items: 1,
      },
      576: {
        items: 1,
      },
      768: {
        items: 1,
      },
      992: {
        items: 2,
      },
      1200: {
        items: 2,
      },
    },
  });

  // vegetable carousel
  $(".vegetable-carousel").owlCarousel({
    autoplay: true,
    smartSpeed: 1500,
    center: false,
    dots: true,
    loop: true,
    margin: 25,
    nav: true,
    navText: [
      '<i class="bi bi-arrow-left"></i>',
      '<i class="bi bi-arrow-right"></i>',
    ],
    responsiveClass: true,
    responsive: {
      0: {
        items: 1,
      },
      576: {
        items: 1,
      },
      768: {
        items: 2,
      },
      992: {
        items: 3,
      },
      1200: {
        items: 4,
      },
    },
  });

  //handle filter products
  $("#btnFilter").onclick(function (event) {
    event.preventDefault();

    let categoryArr = [];
    let priceArr = [];
    let semesterArr = [];

    // Category filter (Kĩ thuật phần mềm, Toán cao cấp, Ngôn ngữ)
    $("#categoryFilter .form-check-input:checked").each(function () {
      let value = $(this).val();

      // Map giá trị từ checkbox sang name parameter
      if (value === "all") {
        categoryArr.push(""); // Kĩ thuật phần mềm
      } else if (value === "math") {
        categoryArr.push("Math"); // Toán cao cấp
      } else if (value === "language") {
        categoryArr.push("Language"); // Ngôn ngữ
      }
    });

    // Price filter (Khóa học miễn phí)
    $("#priceFilter .form-check-input:checked").each(function () {
      priceArr.push($(this).val());
    });

    // Semester filter (Nền tảng, Chuyên sâu)
    $("#semesterFilter .form-check-input:checked").each(function () {
      semesterArr.push($(this).val());
    });

    const currentUrl = new URL(window.location.href);
    const searchParams = currentUrl.searchParams;

    // Add or update query parameters
    searchParams.set("page", "1");

    // Reset các filter cũ
    searchParams.delete("name");
    searchParams.delete("price");
    searchParams.delete("semester");

    // Thêm filter category/name
    if (categoryArr.length > 0) {
      searchParams.set("name", categoryArr.join(","));
    }

    // Thêm filter price
    if (priceArr.length > 0) {
      searchParams.set("price", priceArr.join(","));
    }

    // Thêm filter semester
    if (semesterArr.length > 0) {
      searchParams.set("semester", semesterArr.join(","));
    }

    // Update the URL and reload the page
    window.location.href = currentUrl.toString();
  });

  //handle auto checkbox after page loading
  // Parse the URL parameters
  const params = new URLSearchParams(window.location.search);

  // Set checkboxes for 'factory'
  if (params.has("factory")) {
    const factories = params.get("factory").split(",");
    factories.forEach((factory) => {
      $(`#factoryFilter .form-check-input[value="${factory}"]`).prop(
        "checked",
        true
      );
    });
  }

  // Set checkboxes for 'target'
  if (params.has("target")) {
    const targets = params.get("target").split(",");
    targets.forEach((target) => {
      $(`#targetFilter .form-check-input[value="${target}"]`).prop(
        "checked",
        true
      );
    });
  }

  // Set checkboxes for 'price'
  if (params.has("price")) {
    const prices = params.get("price").split(",");
    prices.forEach((price) => {
      $(`#priceFilter .form-check-input[value="${price}"]`).prop(
        "checked",
        true
      );
    });
  }

  // Set radio buttons for 'sort'
  if (params.has("sort")) {
    const sort = params.get("sort");
    $(`input[type="radio"][name="radio-sort"][value="${sort}"]`).prop(
      "checked",
      true
    );
  }

  $("#searchForm").onclick("click", function (event) {
    event.preventDefault();

    const keyword = $("#searchInput").val().trim();

    const currentUrl = new URL(window.location.href);
    const searchParams = currentUrl.searchParams;

    // Reset page về 1 khi search
    searchParams.set("page", "1");

    // Xóa keyword cũ
    searchParams.delete("keyword");

    // Nếu có nhập thì thêm vào URL
    if (keyword !== "") {
      searchParams.set("name", keyword);
    }

    // Redirect
    window.location.href = currentUrl.toString();
  });

  // =========================
  // Auto fill search input after reload
  // =========================
})(jQuery);
