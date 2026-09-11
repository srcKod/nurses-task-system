<?php

namespace App\Http\Controllers;

use App\Http\Resources\NurseResource;
use App\Models\Nurse;
use App\Traits\ApiResponseTrait;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;

class AuthController extends Controller
{
    use ApiResponseTrait;
    /**
     * Create a new AuthController instance.
     *
     * @return void
     */
    public function __construct() {
        $this->middleware('api', ['except' => ['login', 'register']]);
    }
    /**
     * Get a JWT via given credentials.
     *
     * @return \Illuminate\Http\JsonResponse
     */
    /**
     * @OA\Post(
     *   path="/api/auth/login",
     *   tags={"Auth"},
     *   summary="Nurse login",
     *   description="Nurse login",
     *   operationId="login",
     *   @OA\RequestBody(
     *     description="Nurse login",
     *     required=true,
     *     @OA\JsonContent(
     *      @OA\Property(
     *       title="Email",
     *       property="email",
     *       type="string",
     *       example="rami@example.com"
     *      ),
     *     @OA\Property(
     *       title="Password",
     *       property="password",
     *       type="string",
     *       example="12345678"
     *      ),
     *     ),
     *   ),
     *  @OA\Response(response="201",description="login succeeded",
     *     @OA\JsonContent(
     *      @OA\Property(
     *       title="Access Token",
     *       property="access_token",
     *       type="string",
     *       example="20d338931e8d6bd9466edeba7dfsdasg8365ev7wa75opasSsa5698ea7dce7c7bc01aa5cc5b4735691c50a2fe3228"
     *      ),
     *     @OA\Property(
     *       title="Token Type",
     *       property="token_type",
     *       type="string",
     *     example="bearer"
     *      ),
     *      @OA\Property(
     *       title="Expire in",
     *       property="expires_in",
     *       type="integer",
     *     example="3600"
     *      ),
     *     ),
     *    ),
     *  ),
     *  @OA\Response(response=400,description="Bad Request"),
     *  @OA\Response(response=422,description="Validation exception"),
     * )
     */
    public function login(Request $request){
        $validator = Validator::make($request->all(), [
            'email' => 'required|email',
            'password' => 'required|string|min:8',
        ]);
        if ($validator->fails()) {
            return response()->json($validator->errors(), 422);
        }
        if (! $token = auth('api')->attempt($validator->validated())) {
            return response()->json(['error' => 'Unauthorized'], 401);
        }
        return $this->createNewToken($token);
    }
    /**
     * Register a Nurse.
     *
     * @return \Illuminate\Http\JsonResponse
     */
    /**
     * @OA\Post(
     *   path="/api/auth/register",
     *   tags={"Auth"},
     *   summary="Register new Nurse",
     *   description="Register new Nurse",
     *   operationId="register",
     *   @OA\RequestBody(
     *     description="Register new Nurse",
     *     required=true,
     *     @OA\JsonContent(ref="#/components/schemas/Nurse")
     *   ),
     *  @OA\Response(response="201",description="Registration succeeded"),
     *  @OA\Response(response=400,description="Bad Request"),
     *  @OA\Response(response=422,description="Validation exception"),
     * )
     */
    public function register(Request $request) {
        $validator = Validator::make($request->all(), [
            'name' => 'required|string|between:2,50',
            'email' => 'required|string|email|max:255|unique:nurses',
            //'password' => 'required|string|confirmed|min:8',
            'password' => 'required|string|min:8',
        ]);
        if($validator->fails()){
            return response()->json($validator->errors()->toJson(), 400);
        }
        $nurse = Nurse::create(array_merge(
            $validator->validated(),
            [
				'is_admin' => false,
				'password' => bcrypt($request->password)
			]
        ));
        return response()->json(['message' => 'Registration succeeded'], 201);
    }
    /**
     * Log the Nurse out (Invalidate the token).
     *
     * @return \Illuminate\Http\JsonResponse
     */
    /**
     * @OA\Post(
     *   path="/api/auth/logout",
     *   tags={"Auth"},
     *   summary="Nurse logout",
     *   description="Nurse logout",
     *   operationId="logout",
     *  @OA\Response(response="201",description="successfully signed out"),
     *  @OA\Response(response=400,description="Bad Request"),
     *  @OA\Response(response=422,description="Validation exception"),
     *  security={{ "apiAuth": {} }}
     * )
     */
    public function logout() {
        auth('api')->logout();
        return response()->json(['message' => 'successfully signed out']);
    }
    /**
     * Refresh a token.
     *
     * @return \Illuminate\Http\JsonResponse
     */
    /**
     * @OA\Post(
     *   path="/api/auth/refresh",
     *   tags={"Auth"},
     *   summary="Refresh token",
     *   description="Refresh token",
     *   operationId="refresh",
     *  @OA\Response(response="201",description="Refresh succeeded",
     *     @OA\JsonContent(
     *      @OA\Property(
     *       title="Access Token",
     *       property="access_token",
     *       type="string",
     *       example="20d338931e8d6bd9466edeba7dfsdasg8365ev7wa75opasSsa5698ea7dce7c7bc01aa5cc5b4735691c50a2fe3228"
     *      ),
     *     @OA\Property(
     *       title="Token Type",
     *       property="token_type",
     *       type="string",
     *     example="bearer"
     *      ),
     *      @OA\Property(
     *       title="Expire in",
     *       property="expires_in",
     *       type="integer",
     *     example="3600"
     *      ),
     *     ),
     *  ),
     *  @OA\Response(response=400,description="Bad Request"),
     *  security={{ "apiAuth": {} }}
     * )
     */
    public function refresh() {
        return $this->createNewToken(auth('api')->refresh());
    }
    /**
     * Get the authenticated Nurse.
     *
     * @return \Illuminate\Http\JsonResponse
     */
    /**
     * @OA\Get(
     *   path="/api/auth/nurseProfile",
     *   tags={"Auth"},
     *   summary="Nurse Profile",
     *   description="Nurse Profile",
     *   operationId="nurseProfile",
     *  @OA\Response(response="200",description="ok",
     *     @OA\JsonContent(
     *      @OA\Property(
     *       title="Nurse",
     *       property="Nurse",
     *       type="object",
     *       ref="#/components/schemas/Nurse"
     *      ),
     *    ),
     *  ),
     *  @OA\Response(response=400,description="Bad Request"),
     *  security={{ "apiAuth": {} }}
     * )
     */
    public function nurseProfile() {
        if(!auth('api')->check()){
            return $this->apiResponse(null,401,"Unuthorized");
        }
        return $this->apiResponse(new NurseResource(auth('api')->user()),200,"ok");
    }
    /**
     * Get the token array structure.
     *
     * @param  string $token
     *
     * @return \Illuminate\Http\JsonResponse
     */
    protected function createNewToken($token){
        return response()->json([
            'access_token' => $token,
            'token_type' => 'bearer',
            'expires_in' => auth('api')->factory()->getTTL() * 60,
//            'nurse' => auth('api')->user()
        ]);
    }
}
