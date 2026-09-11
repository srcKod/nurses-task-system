<?php

namespace App\Http\Middleware;

use App\Traits\ApiResponseTrait;
use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class CheckAdmin
{
    use ApiResponseTrait;
    /**
     * Handle an incoming request.
     *
     * @param  \Closure(\Illuminate\Http\Request): (\Symfony\Component\HttpFoundation\Response)  $next
     */
    public function handle(Request $request, Closure $next): Response
    {
//        if (auth()->user()?->getAttributeValue('is_admin') == 0) {
//            return abort(401);
//        }
        if (auth()->user()?->getAttributeValue('is_admin') == 0) {
            return $this->apiResponse(null,401,'you are not authorized');
        }
        return $next($request);
    }
}
