<!DOCTYPE html>
<html lang="{{ config('app.locale') }}">
<head>
    <title>{{ config('app.name', 'Laravel') }}</title>
<link rel="icon" 
      type="image/png" 
      href="{{ URL::asset('images/logo.png') }}" />
<link rel="stylesheet" href="{{ URL::asset('css/bootstrap.css') }}">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<meta name="csrf-token" content="{{ csrf_token() }}">

<!-- Custom Theme files -->
<link rel="stylesheet" href="{{ URL::asset('css/style.css') }}">
<!-- Custom Theme files -->
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="application/x-javascript"> addEventListener("load", function() { setTimeout(hideURLbar, 0); }, false); function hideURLbar(){ window.scrollTo(0,1); }>
</script>
<script>
        window.Laravel = <?php echo json_encode([
            'csrfToken' => csrf_token(),
        ]); ?>
</script>
<meta name="keywords" content="Sopnorpiyo, Shahin Alam, Md Shahin Alam" />
<!--Google Fonts-->
<!-- start-smoth-scrolling -->
<script type="text/javascript" src="js/move-top.js"></script>
<script type="text/javascript" src="js/easing.js"></script>
    <script type="text/javascript">
            jQuery(document).ready(function($) {
                $(".scroll").click(function(event){     
                    event.preventDefault();
                    $('html,body').animate({scrollTop:$(this.hash).offset().top},1000);
                });
            });
</script>
<!-- //end-smoth-scrolling -->  
<!---pop-up-box---->
                    <link href="css/popuo-box.css" rel="stylesheet" type="text/css" media="all"/>
                    <script src="{{ URL::asset('js/jquery.magnific-popup.js') }}" type="text/javascript"></script>
                    <!---//pop-up-box---->
                     <script>
                        $(document).ready(function() {
                        $('.popup-with-zoom-anim').magnificPopup({
                            type: 'inline',
                            fixedContentPos: false,
                            fixedBgPos: true,
                            overflowY: 'auto',
                            closeBtnInside: true,
                            preloader: false,
                            midClick: true,
                            removalDelay: 300,
                            mainClass: 'my-mfp-zoom-in'
                        });
                                                                                        
                        });
                </script>
</head>
<body>
    <div id="app">
        <nav class="navbar navbar-frontend  navbar-static-top">
            <div class="container">
                <div class="navbar-header">

                    <!-- Collapsed Hamburger -->
                    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#app-navbar-collapse">
                        <span class="sr-only">Toggle Navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>

                    <!-- Branding Image -->
                    <a class="navbar-brand" href="{{ url('/') }}">
                        {{ config('app.name', 'Laravel') }}
                    </a>
                </div>

                <div class="collapse navbar-collapse" id="app-navbar-collapse">
                    <!-- Left Side Of Navbar -->
                    <ul class="nav navbar-nav">
                        <li><a class="scroll active" href="#home">Home</a></li> 
                        <li><a class="scroll" href="#about">About</a></li> 
                        <li><a class="scroll" href="#contact">Contact</a></li> 
                    </ul>

                    <!-- Right Side Of Navbar -->
                    <ul class="nav navbar-nav navbar-right">
                        <!-- Authentication Links -->
                        @if (Auth::guest())
                            <li><a href="{{ route('login') }}">Login</a></li>
                        @else
                             <li class="dropdown">
                                <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
                                    {{ Auth::user()->name }} <span class="caret"></span>
                                </a>

                                <ul class="dropdown-menu" role="menu">
                                    <li>
                                        <a href="{{ route('logout') }}"
                                            onclick="event.preventDefault();
                                                     document.getElementById('logout-form').submit();">
                                            Logout
                                        </a>

                                        <form id="logout-form" action="{{ route('logout') }}" method="POST" style="display: none;">
                                            {{ csrf_field() }}
                                        </form>
                                    </li>
                                </ul>
                            </li>
                        @endif
                    </ul>

                      <script>
                           $( "span.menu" ).click(function() {
                             $( "ul.res" ).slideToggle( 300, function() {
                             // Animation complete.
                              });
                             });
                        </script>

                       
                </div>
            </div>
        </nav>

        
           <div id="mySidenav" class="sidenav">
              <button type="button" class="close" data-dismiss="modal" onclick="closeNav()" aria-label="Close"><span aria-hidden="true" >&times; Close</span></button>
              <a href="/dashboard">Dashboard</a>
              <a href="/message">Message</a>
              <a href="#">Clients</a>
              <a href="#">Contact</a>
            </div>

        @yield('content')
    </div>

    <!-- Scripts -->
    <script src="{{ asset('js/app.js') }}"></script>
      <script src="{{ asset('js/custom.js') }}"></script>
</body>
</html>
