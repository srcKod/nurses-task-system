<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Http\Requests\CaringtypeStoreRequest;
use App\Http\Resources\CaringtypeResource;
use App\Models\Caringtype;
use App\Traits\ApiResponseTrait;
use App\Traits\CaringTypeTrait;
use Illuminate\Http\Request;

class CaringtypeController extends Controller
{
    use CaringTypeTrait, ApiResponseTrait;

    /**
     * @OA\Get(
     *     path="/api/caringtypes",
     *     tags={"Caringtype"},
     *     summary="Get Caringtypes",
     *     description="Get Caringtypes",
     *     operationId="getCaringTypes",
     *     deprecated=false,
     *     @OA\Response(response=200,description="ok",
     *       @OA\JsonContent(
     *         type="array",
     *         @OA\Items(ref="#/components/schemas/Caringtype")
     *      )
     *    ),
     *   security={{ "apiAuth": {} }}
     * )
     */
    public function getCaringTypes()
    {
        $caringtypes = CaringtypeResource::collection(Caringtype::get());
        if($caringtypes->isEmpty()){
            return $this->apiResponse(null, 404,'No Caringtypes was found');
        }
        return $this->apiResponse($caringtypes, 200,'ok');
    }

    /**
     * @OA\Get(
     *     path="/api/caringtypes/{id}",
     *     tags={"Caringtype"},
     *     summary="Get Caringtype",
     *     description="Caringtype id should be provided",
     *     operationId="getCaringType",
     *     @OA\Parameter(
     *       name="id",
     *       in="path",
     *       description="Caringtype Id",
     *       required=true,
     *       @OA\Schema(
     *         type="integer",
     *         format="int64"
     *       )
     *     ),
     *     @OA\Response(response=200, description="ok",
     *       @OA\JsonContent(
     *         type="array",
     *         @OA\Items(ref="#/components/schemas/Caringtype")
     *       )
     *     ),
     *    @OA\Response(response="404",description="Caringtype not found."),
     *   security={{ "apiAuth": {} }}
     * )
     */
    public function getCaringType($id)
    {
        $caringtype = Caringtype::find($id);
        if(!$caringtype){
            $response = $this->apiResponse(null, 404,'A Caringtype with specified id not found');
        }else{
            return $this->apiResponse(new CaringtypeResource($caringtype), 200,'ok');
        }
        return $response;
    }

    /**
     * @OA\Post(
     *   path="/api/caringtypes",
     *   tags={"Caringtype"},
     *   summary="Create Caringtype",
     *   description="Multiple values can be provided with comma separated string",
     *   operationId="createCaringType",
     *   @OA\RequestBody(
     *     description="Caringtype to create",
     *     required=true,
     *     @OA\JsonContent(ref="#/components/schemas/Caringtype")
     *   ),
     *  @OA\Response(response="201",description="Caringtype created",
     *   @OA\JsonContent(
     *     @OA\Property(
     *      title="data",
     *      property="data",
     *      type="object",
     *      ref="#/components/schemas/Caringtype"
     *     ),
     *    ),
     *  ),
     *  @OA\Response(response=400,description="Bad Request"),
     *  @OA\Response(response=422,description="Validation exception"),
     *  security={{ "apiAuth": {} }}
     * )
     */
    public function createCaringType(CaringtypeStoreRequest $request)
    {
        $caringtype = new Caringtype();
        $this->saveRequestData($request, $caringtype);
        return $this->apiResponse(new CaringtypeResource($caringtype),200,'ok');
    }

    /**
     * @OA\Patch(
     *   path="/api/caringtypes",
     *   tags={"Caringtype"},
     *   summary="Update Caringtype",
     *   description="Update Caringtype",
     *   operationId="updateCaringType",
     *   @OA\Response(response="201",description="Caringtype updated",
     *     @OA\JsonContent(
     *      @OA\Property(
     *       title="data",
     *       property="data",
     *       type="object",
     *       ref="#/components/schemas/Caringtype"
     *       ),
     *     ),
     *   ),
     *   @OA\Response(response="400",description="Bad request."),
     *   @OA\Response(response="404",description="Caringtype not found."),
     *   @OA\RequestBody(
     *     description="Caringtype to update",
     *     required=true,
     *     @OA\JsonContent(ref="#/components/schemas/Caringtype")
     *   ),
     *   security={{ "apiAuth": {} }}
     * )
     */
    public function updateCaringType(Request $request)
    {
        $caringtype = Caringtype::find($request->id);
        if(!$caringtype){
            return $this->apiResponse(null, 404,'A Caringtype with specified id not found');
        }
        $this->saveRequestData($request, $caringtype);
        return $this->apiResponse(new CaringtypeResource($caringtype),200,'ok');
    }

    /**
     * @OA\Delete(
     *  path="/api/caringtypes/{id}",
     *  summary="Delete Caringtype",
     *  description="Delete Caringtype",
     *  operationId="deleteCaringType",
     *  tags={"Caringtype"},
     *  @OA\Parameter(ref="#/components/parameters/Caringtype--id"),
     *  @OA\Response(response=200,description="ok"),
     *  @OA\Response(response=401,description="Unauthorized"),
     *  @OA\Response(response=404,description="Caringtype not found"),
     *  security={{ "apiAuth": {} }}
     * )
     */
    public function deleteCaringType($id)
    {
        $caringtype = Caringtype::find($id);
        if(!$caringtype){
            return $this->apiResponse(null, 404,'A Caringtype with specified id not found');
        }
        $caringtype->delete();
        return $this->apiResponse(new CaringtypeResource($caringtype), 200,'ok');
    }
}
