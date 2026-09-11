<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;

/**
 * @OA\Info(
 *      version="1.0.0",
 *      x={
 *          "logo": {
 *              "url": "https://via.placeholder.com/190x90.png?text=L5-Swagger"
 *          }
 *      },
 *      title="E-Nurse Api",
 *      description="E-Nurse Api description",
 *      @OA\Contact(
 *          email="developer@example.com"
 *      ),
 *     @OA\License(
 *         name="Apache 2.0",
 *         url="https://www.apache.org/licenses/LICENSE-2.0.html"
 *     )
 * ),
*  @OA\SecurityScheme(
*   type="http",
*   description="Login with email and password to get the authentication token",
*   name="Token based Based",
*   in="header",
*   scheme="bearer",
*   bearerFormat="JWT",
*   securityScheme="apiAuth",
 * )
 */
class ApiController extends Controller
{
    //
}
